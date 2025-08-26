package project.flipnote.group.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.response.CursorPagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.entity.GroupPermission;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.entity.GroupRolePermission;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.model.FindGroupMemberResponse;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.model.GroupDetailResponse;
import project.flipnote.group.model.GroupInfo;
import project.flipnote.group.model.GroupListRequest;
import project.flipnote.group.model.GroupMemberInfo;
import project.flipnote.group.model.GroupPutRequest;
import project.flipnote.group.model.GroupPutResponse;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupPermissionRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.group.repository.GroupRolePermissionRepository;
import project.flipnote.groupjoin.exception.GroupJoinErrorCode;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserProfileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

	private static final int SIZE = 10;

	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final GroupPermissionRepository groupPermissionRepository;
	private final GroupRolePermissionRepository groupRolePermissionRepository;
	private final UserProfileRepository userProfileRepository;
	private final GroupPolicyService groupPolicyService;

	/*
	유저 정보 조회
	 */
	public UserProfile getUser(AuthPrinciple authPrinciple) {
		return userProfileRepository.findByIdAndStatus(authPrinciple.userId(), UserStatus.ACTIVE).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	/*
	유저 정보 조회
	 */
	public void validateUser(AuthPrinciple authPrinciple) {
		if(!userProfileRepository.existsByIdAndStatus(authPrinciple.userId(), UserStatus.ACTIVE)) {
			throw new BizException(UserErrorCode.USER_NOT_FOUND);
		}
	}

	/*
	그룹 내 유저 검증
	 */
	public void validateGroupInUser(UserProfile user, Long groupId) {
		if (!groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, user.getId())) {
			throw new BizException(GroupJoinErrorCode.USER_NOT_IN_GROUP);
		}
	}

	/*
	그룹 내 유저 조회
	 */
	public GroupMember getGroupMember(UserProfile user, Long groupId) {
		return groupMemberRepository.findByGroup_IdAndUser_Id(groupId, user.getId()).orElseThrow(
			() -> new BizException(GroupJoinErrorCode.USER_NOT_IN_GROUP)
		);
	}

	/*
	그룹 검증
	 */
	public void validateGroup(Long groupId) {
		if(!groupRepository.existsByIdAndDeletedAtIsNull(groupId)) {
			throw new BizException(GroupErrorCode.GROUP_NOT_FOUND);
		}
	}

	/*
	그룹 조회
	 */
	public Group getGroup(Long groupId) {
		return groupRepository.findByIdAndDeletedAtIsNull(groupId).orElseThrow(
			() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND)
		);
	}

	/*
	그룹 생성
	 */
	@Transactional
	public GroupCreateResponse create(AuthPrinciple authPrinciple, GroupCreateRequest req) {

		//1. 유저 조회
		UserProfile user = getUser(authPrinciple);

		//2. 인원수 검증
		validateMaxMember(req.maxMember());

		/* 3. 그룹 생성 */
		Group group = createGroup(req);

		//4. 그룹 회원 정보 생성
		saveGroupOwner(group, user);

		//5. 그룹 내의 모든 권한 생성
		initializeGroupPermissions(group);

		return GroupCreateResponse.from(group.getId());
	}

	public Boolean hasPermission(Long groupId, Long userId, GroupPermissionStatus groupPermissionStatus) {
		GroupMember groupMember = groupMemberRepository.findByGroup_IdAndUser_Id(groupId, userId).orElseThrow(
			() -> new BizException(GroupErrorCode.USER_NOT_IN_GROUP)
		);

		GroupPermission groupPermission = groupPermissionRepository.findByName(groupPermissionStatus);

		return groupRolePermissionRepository.existsByGroupAndRoleAndGroupPermission(
			groupRepository.getReferenceById(groupId),
			groupMember.getRole(),
			groupPermission
		);
	}

	/*
	최초 그룹 권한 설정
	 */
	private void initializeGroupPermissions(Group group) {
		List<GroupPermission> groupPermissions = groupPermissionRepository.findAll();

		List<GroupRolePermission> groupRolePermissions = Arrays.stream(GroupMemberRole.values())
			.flatMap(role -> groupPermissions.stream()
				.map(permission -> GroupRolePermission.builder()
					.group(group)
					.groupPermission(permission)
					.role(role)
					.build()))
			.toList();

		groupRolePermissionRepository.saveAll(groupRolePermissions);
	}

	/*
	그룹 생성 메서드
	 */
	private Group createGroup(GroupCreateRequest req) {
		Group group = Group.builder()
			.name(req.name())
			.category(req.category())
			.description(req.description())
			.applicationRequired(req.applicationRequired())
			.publicVisible(req.publicVisible())
			.maxMember(req.maxMember())
			.imageUrl(req.image())
			.build();

		return groupRepository.save(group);
	}

	/*
	그룹 생성시 오너 멤버 추가
	 */
	private void saveGroupOwner(Group group, UserProfile user) {
		GroupMember groupMember = GroupMember.builder()
			.group(group)
			.user(user)
			.role(GroupMemberRole.OWNER)
			.build();

		groupMemberRepository.save(groupMember);
	}

	/*
	인원수 검증
	 */
	private void validateMaxMember(int maxMember) {
		if (maxMember < 1 || maxMember > 100) {
			throw new BizException(GroupErrorCode.INVALID_MAX_MEMBER);
		}
	}

	//유저수 검증
	private void validateUserCount(Group group, int maxMember) {
		if (group.getMemberCount() > maxMember) {
			throw new BizException(GroupErrorCode.INVALID_MEMBER_COUNT);
		}
	}

	//그룹 수정
	@Transactional
	public GroupPutResponse changeGroup(AuthPrinciple authPrinciple, GroupPutRequest req, Long groupId) {

		//1. 유저 조회
		UserProfile user = getUser(authPrinciple);

		//2. 인원수 검증
		validateMaxMember(req.maxMember());

		//3. 그룹 조회
		validateGroup(groupId);

		//4. 그룹 내 유저 조회
		GroupMember groupMember = getGroupMember(user, groupId);

		//5. 유저 권환 조회
		if (!groupMember.getRole().equals(GroupMemberRole.OWNER)) {
			throw new BizException(GroupErrorCode.USER_NOT_PERMISSION);
		}

		//6. 그룹 수정
		Group changeGroup = groupPolicyService.changeGroup(groupId, req);

		return GroupPutResponse.from(changeGroup);
	}
	/*
	그룹 내 오너를 제외한 인원이 존재하는 경우 체크
	 */
	private boolean checkUserNotExistInGroup(UserProfile user, Group group) {
		long count = groupMemberRepository.countByGroup_idAndUser_idNot(group.getId(), user.getId());
		if (count > 0) {
			return false;
		}
		return true;
	}

	/*
	그룹 내 모든 멤버리스트 조회
	 */
	private List<GroupMemberInfo> findGroupMembers(Long groupId) {
		//각 그룹멤버의 id를 가지고 유저를 찾고 유저명, 권한, 등등 가져오기
		return groupMemberRepository.findGroupMembers(groupId);
	}

	/*
	그룹 상세 정보 조회
	 */
	public GroupDetailResponse findGroupDetail(AuthPrinciple authPrinciple, Long groupId) {

		//1. 그룹 조회
		Group group = getGroup(groupId);

		//2. 유저 조회
		UserProfile user = getUser(authPrinciple);

		//3. 그룹 내 유저 조회
		validateGroupInUser(user, groupId);

		return GroupDetailResponse.from(group);
	}

	//그룹 삭제 메서드
	@Transactional
	public void deleteGroup(AuthPrinciple authPrinciple, Long groupId) {
		//1. 그룹 조회
		Group group = getGroup(groupId);

		//2. 유저 조회
		UserProfile user = getUser(authPrinciple);

		//3. 그룹 내 유저 조회
		GroupMember groupMember = getGroupMember(user, groupId);

		//4. 유저 권환 조회
		if (!groupMember.getRole().equals(GroupMemberRole.OWNER)) {
			throw new BizException(GroupErrorCode.USER_NOT_PERMISSION);
		}

		//5. 오너를 제외한 모든 유저가 없어야 삭제 가능
		if (!checkUserNotExistInGroup(user, group)) {
			throw new BizException(GroupErrorCode.OTHER_USER_EXIST_IN_GROUP);
		}

		groupMemberRepository.delete(groupMember);

		groupRepository.delete(group);

	}

	//그룹이름 찾는 메서드
	public String findGroupName(Long groupId) {
		return groupRepository.findGroupNameById(groupId)
			.orElseThrow(() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND));
	}

	//그룹 내 멤버 조회 메서드
	public FindGroupMemberResponse findGroupMembers(AuthPrinciple authPrinciple, Long groupId) {
		//1. 그룹 검증
		validateGroup(groupId);

		//2. 유저 조회
		UserProfile user = getUser(authPrinciple);

		//3. 그룹 내 유저 조회
		validateGroupInUser(user, groupId);

		//4. 그룹 내 모든 유저 조회
		List<GroupMemberInfo> groupMembers = findGroupMembers(groupId);

		return FindGroupMemberResponse.from(groupMembers);
	}

	public CursorPagingResponse<GroupInfo> findGroup(AuthPrinciple authPrinciple, GroupListRequest req) {
		//1. 유저 검증
		validateUser(authPrinciple);

		//2. 카테고리 변환
		Category category = convertCategory(req.getCategory());

		List<GroupInfo> groups = groupRepository.findAllByCursor(req.getCursorId(), category, req.getSize());

		boolean hasNext = groups.size() > SIZE;

		if (hasNext) {
			groups = groups.subList(0, SIZE);
		}

		Long nextCursor = hasNext ? groups.get(groups.size() - 1).groupId() : null;

		return CursorPagingResponse.of(groups, hasNext, nextCursor);
	}

	private Category convertCategory(String rawCategory) {
		Category category = null;
		if (rawCategory != null && !rawCategory.isBlank()) {
			try {
				category = Category.valueOf(rawCategory.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new BizException(GroupErrorCode.INVALID_CATEGORY);
			}
		}
		return category;
	}

	/**
	 * 해당 회원에 그룹에 존재하는지 확인
	 *
	 * @param groupId 검증할 그룹의 ID
	 * @param userId 검증할 회원의 ID
	 * @return 회원이 그룹 멤버인지 여부
	 * @author 윤정환
	 */
	public boolean existsMember(Long groupId, Long userId) {
		return groupMemberRepository.existsByGroup_IdAndUser_Id(groupId, userId);
	}
}
