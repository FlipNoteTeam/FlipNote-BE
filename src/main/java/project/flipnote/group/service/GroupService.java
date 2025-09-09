package project.flipnote.group.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
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
import project.flipnote.image.entity.Image;
import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.entity.ImageStatus;
import project.flipnote.image.entity.ReferenceType;
import project.flipnote.image.exception.ImageErrorCode;
import project.flipnote.image.service.ImageRefService;
import project.flipnote.image.service.ImageUploadService;
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
	private static final ReferenceType REFERENCE_TYPE = ReferenceType.GROUP;

	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final GroupPermissionRepository groupPermissionRepository;
	private final GroupRolePermissionRepository groupRolePermissionRepository;
	private final UserProfileRepository userProfileRepository;
	private final GroupPolicyService groupPolicyService;
	private final ImageUploadService imageUploadService;
	private final ImageRefService imageRefService;

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

	private List<GroupPermission> getOrCreateGroupPermissions() {
		List<GroupPermission> all = groupPermissionRepository.findAll();

		if (all.size() == GroupPermissionStatus.values().length) {
			return all;
		}

		Set<GroupPermissionStatus> existing = all.stream()
			.map(GroupPermission::getName)
			.collect(Collectors.toSet());

		List<GroupPermission> missing = Arrays.stream(GroupPermissionStatus.values())
			.filter(s -> !existing.contains(s))
			.map(s -> GroupPermission.builder().name(s).build())
			.toList();

		if (!missing.isEmpty()) {
			try {
				groupPermissionRepository.saveAll(missing);
			} catch (DataIntegrityViolationException ignore) {
				// 다른 트랜잭션이 먼저 넣은 경우: 무시하고 재조회
			}
			all = groupPermissionRepository.findAll();
		}
		return all;
	}

	/**
	 * 그룹 생성
	 * @param authPrinciple 회원 accessToken
	 * @param req 그룹 생성시 필요한 내용
	 * @return
	 */
	@Transactional
	public GroupCreateResponse create(AuthPrinciple authPrinciple, GroupCreateRequest req) {

		//1. 유저 조회
		UserProfile user = getUser(authPrinciple);

		//2. 인원수 검증
		validateMaxMember(req.maxMember());

		ImageRef refId = imageRefService.findById(req.imageRefId()).orElseThrow(
			() -> new BizException(ImageErrorCode.IMAGE_NOT_FOUND)
		);

		Image image = refId.getImage();

		String url = imageUploadService.generateUrl(image.getS3Key()).toString();

		//3. 그룹 생성
		Group group = createGroup(req, url);

		//4. 그룹 회원 정보 생성
		saveGroupOwner(group, user);

		//5. 그룹 내의 모든 권한 생성
		initializeGroupPermissions(group);

		// 이미지 활성화
		imageUploadService.changeUrlStatus(req.imageRefId(), REFERENCE_TYPE, group.getId());

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
		List<GroupPermission> groupPermissions = getOrCreateGroupPermissions();

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
	private Group createGroup(GroupCreateRequest req, String url) {
		Group group = Group.builder()
			.name(req.name())
			.category(req.category())
			.description(req.description())
			.applicationRequired(req.applicationRequired())
			.publicVisible(req.publicVisible())
			.maxMember(req.maxMember())
			.imageUrl(url)
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

	/**
	 * 그룹 수정
	 * @param authPrinciple
	 * @param req
	 * @param groupId
	 * @return
	 */
	@Transactional
	public GroupPutResponse changeGroup(AuthPrinciple authPrinciple, GroupPutRequest req, Long groupId) {

		//유저 조회
		UserProfile user = getUser(authPrinciple);

		//인원수 검증
		validateMaxMember(req.maxMember());

		//그룹 조회
		validateGroup(groupId);

		//그룹 내 유저 조회
		GroupMember groupMember = getGroupMember(user, groupId);

		//유저 권환 조회
		if (!groupMember.getRole().equals(GroupMemberRole.OWNER)) {
			throw new BizException(GroupErrorCode.USER_NOT_PERMISSION);
		}

		//이미지 변경
		String url = imageUploadService.changeImage(ReferenceType.GROUP, groupId, req.imageRefId());

		//그룹 수정
		Group changeGroup = groupPolicyService.changeGroup(groupId, req, url);
		
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

		return createGroupInfoCursorPagingResponse(req, groups);
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

	/**
	 * 내가 가입한 그룹 전체 조회하기
	 * 
	 * @param authPrinciple 회원 accessToken
	 * @param req 필터링
	 * @return
	 */
	public CursorPagingResponse<GroupInfo> findMyGroup(AuthPrinciple authPrinciple, GroupListRequest req) {
		//1. 유저 가져오기
		UserProfile user = getUser(authPrinciple);

		//2. 카테고리 변환
		Category category = convertCategory(req.getCategory());

		List<GroupInfo> groups = groupRepository.findAllByCursorAndUserId(req.getCursorId(), category, req.getSize(),
			user.getId());

		return createGroupInfoCursorPagingResponse(req, groups);
	}

	//리스트 조회시 response 생성
	private CursorPagingResponse<GroupInfo> createGroupInfoCursorPagingResponse(GroupListRequest req,
		List<GroupInfo> groups) {
		boolean hasNext = groups.size() > req.getSize();

		if (hasNext) {
			groups = groups.subList(0, req.getSize());
		}

		Long nextCursor = hasNext ? groups.get(groups.size() - 1).groupId() : null;

		return CursorPagingResponse.of(groups, hasNext, nextCursor);
	}
}
