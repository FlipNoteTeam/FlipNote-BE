package project.flipnote.group.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.entity.GroupPermission;
import project.flipnote.group.entity.GroupRole;
import project.flipnote.group.entity.GroupRolePermission;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.repository.GroupPermissionRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.group.repository.GroupRolePermissionRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final GroupPermissionRepository groupPermissionRepository;
	private final GroupRolePermissionRepository groupRolePermissionRepository;
	private final UserRepository userRepository;

	//유저 정보 조회
	public User findUser(UserAuth userAuth) {
		return userRepository.findByIdAndStatus(userAuth.userId(), UserStatus.ACTIVE).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	//그룹 생성
	@Transactional
	public GroupCreateResponse create(UserAuth userAuth, GroupCreateRequest req) {

		//1. 유저 조회
		User user = findUser(userAuth);

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
	
	/*
	최초 그룹 권한 설정
	 */
	private void initializeGroupPermissions(Group group) {
		List<GroupPermission> groupPermissions = groupPermissionRepository.findAll();

		List<GroupRolePermission> groupRolePermissions = Arrays.stream(GroupRole.values())
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

		groupRepository.save(group);
		
        log.info("생성 시간: {}", group.getCreatedDate());

		return group;
	}
	
	/*
	그룹 생성시 오너 멤버 추가
	 */
	private void saveGroupOwner(Group group, User user) {
		GroupMember groupMember = GroupMember.builder()
				.group(group)
				.user(user)
				.role(GroupMemberRole.OWNER)
				.build();

		groupMemberRepository.save(groupMember);
	}

	//인원수 검증
	private void validateMaxMember(int maxMember) {
		if (maxMember < 1 || maxMember > 100) {
			throw new BizException(GroupErrorCode.INVALID_MAX_MEMBER);
		}
	}

}
