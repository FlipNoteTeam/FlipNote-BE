package project.flipnote.group.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.entity.GroupPermission;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.entity.GroupRolePermission;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.model.GroupDetailResponse;
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

	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final GroupPermissionRepository groupPermissionRepository;
	private final GroupRolePermissionRepository groupRolePermissionRepository;
	private final UserProfileRepository userProfileRepository;

	/*
	유저 정보 조회
	 */
	public UserProfile validateUser(AuthPrinciple authPrinciple) {
		return userProfileRepository.findByIdAndStatus(authPrinciple.userId(), UserStatus.ACTIVE).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}
	
	/*
	그룹 내 유저 조회
	 */
	public GroupMember validateGroupInUser(UserProfile user, Long groupId) {
		return groupMemberRepository.findByGroup_IdAndUser_Id(groupId, user.getId()).orElseThrow(
			() -> new BizException(GroupJoinErrorCode.USER_NOT_IN_GROUP)
		);
	}
	
	/*
	그룹 조회
	 */
	public Group validateGroup(Long groupId) {
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
		UserProfile user = validateUser(authPrinciple);

		//2. 인원수 검증
		validateMaxMember(req.maxMember());

		/* 3. 그룹 생성 */
		Group group = createGroup(req);

		//4. 그룹 회원 정보 생성
		saveGroupOwner(group, user);

		//5. 그룹 내의 모든 권한 조회
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

	/*
	그룹 내 오너를 제외한 인원이 존재하는 경우 체크
	 */
	private boolean checkUserNotExistInGroup(UserProfile user, Long groupId) {
		long count = groupMemberRepository.countByGroup_idAndUser_idNot(groupId, user.getId());
		if (count > 0) {
			return false;
		}
		return true;
	}

	/*
	그룹 상세 정보 조회
	 */
	public GroupDetailResponse findGroupDetail(AuthPrinciple authPrinciple, Long groupId) {

		//1. 그룹 조회
		Group group = validateGroup(groupId);

		//2. 유저 조회
		UserProfile user = validateUser(authPrinciple);

		//3. 그룹 내 유저 조회
		validateGroupInUser(user, groupId);

		return GroupDetailResponse.from(group);
	}

	//그룹 삭제 메서드
	@Transactional
	public void deleteGroup(AuthPrinciple authPrinciple, Long groupId) {
		//1. 그룹 조회
		Group group = validateGroup(groupId);

		//2. 유저 조회
		UserProfile user = validateUser(authPrinciple);

		//3. 그룹 내 유저 조회
		GroupMember groupMember = validateGroupInUser(user, groupId);

		//4. 유저 권환 조회
		if (!groupMember.getRole().equals(GroupMemberRole.OWNER)) {
			throw new BizException(GroupErrorCode.USER_NOT_PERMISSION);
		}

		//5. 오너를 제외한 모든 유저가 없어야 삭제 가능
		if (!checkUserNotExistInGroup(user, groupId)) {
			throw new BizException(GroupErrorCode.OTHER_USER_EXIST_IN_GROUP);
		}

		groupRepository.delete(group);

	}

	public String findGroupName(Long groupId) {
		return groupRepository.findGroupNameById(groupId)
			.orElseThrow(() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND));
	}
}
