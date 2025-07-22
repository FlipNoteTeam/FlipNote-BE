package project.flipnote.groupapplication.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.group.entity.*;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupPermissionRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.group.repository.GroupRolePermissionRepository;
import project.flipnote.groupapplication.entity.GroupApplication;
import project.flipnote.groupapplication.entity.GroupApplicationStatus;
import project.flipnote.groupapplication.exception.GroupApplicationErrorCode;
import project.flipnote.groupapplication.model.*;
import project.flipnote.groupapplication.repository.GroupApplicationRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupApplicationService {
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final GroupApplicationRepository groupApplicationRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final GroupRolePermissionRepository groupRolePermissionRepository;
	private final GroupPermissionRepository groupPermissionRepository;

	//유저 정보 조회
	private User findUser(UserAuth userAuth) {
		return userRepository.findByIdAndStatus(userAuth.userId(), UserStatus.ACTIVE).orElseThrow(
				() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	//그룹 정보 조회
	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(
				() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND)
		);
	}
	
	//그룹 내 권한 정보 조회
	private Boolean hasPermission(Group group, User user) {
		GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, user).orElseThrow(
				() -> new BizException(GroupApplicationErrorCode.USER_NOT_IN_GROUP)
		);


		GroupPermission groupPermission = groupPermissionRepository.findByName(GroupPermissionStatus.JOIN_REQUEST_MANAGE);

        return groupRolePermissionRepository.existByGroupAndRoleAndGroupPermission(group, groupMember.getRole(), groupPermission);
	}
	
	private List<GroupApplication> findGroupApplications(Group group) {
        return groupApplicationRepository.findAllByGroup(group);
	}

	private GroupApplication findGroupApplication(Long joinId) {
		return groupApplicationRepository.findById(joinId).orElseThrow(
				() ->  new BizException(GroupApplicationErrorCode.NOT_EXIST_JOIN)
		);
	}
	
	//가입 신청 요청
	@Transactional
	public GroupApplicationJoinResponse joinRequest(UserAuth userAuth, Long groupId, GroupApplicationJoinRequest req) {
		//유저 조회
		User user = findUser(userAuth);
		//그룹 조회
		Group group = findGroup(groupId);

		GroupApplication groupApplication = GroupApplication.builder()
				.group(group)
				.user(user)
				.joinIntro(req.joinIntro())
				.status(GroupApplicationStatus.PENDING)
				.build();

		GroupApplication saveGroupApplication = groupApplicationRepository.save(groupApplication);

		return GroupApplicationJoinResponse.from(saveGroupApplication.getId());
	}

	//그룹 가입 신청 리스트 조회
	public GroupApplicationListResponse findGroupJoinList(UserAuth userAuth, Long groupId) {
		//유저 조회
		User user = findUser(userAuth);

		//그룹 조회
		Group group = findGroup(groupId);
		
		//그룹 내 권한 조회
		Boolean isExistPermission = hasPermission(group, user);
		
		//권한 존재하지 않으면 에러
		if (!isExistPermission) {
			throw new BizException(GroupApplicationErrorCode.USER_NOT_PERMISSION);
		}

		//그룹 내 가입 신청 리스트 조회
		List<GroupApplication> groupApplications = findGroupApplications(group);
		
		//반환
		return GroupApplicationListResponse.from(groupApplications);
	}

	//가입 신청 응답
	public GroupApplicationRespondResponse respondToJoinRequest(UserAuth userAuth, Long groupId, Long joinId, @Valid GroupApplicationRespondRequest req) {
		//유저 조회
		User user = findUser(userAuth);

		//그룹 조회
		Group group = findGroup(groupId);

		//그룹 내 권한 조회
		Boolean isExistPermission = hasPermission(group, user);

		//권한 존재하지 않으면 에러
		if (!isExistPermission) {
			throw new BizException(GroupApplicationErrorCode.USER_NOT_PERMISSION);
		}

		//그룹 가입 신청 조회
		GroupApplication groupApplication = findGroupApplication(joinId);
		
		groupApplication.updateStatus(req.status());

		groupApplicationRepository.save(groupApplication);

		return GroupApplicationRespondResponse.from(groupApplication.getId());
	}
}
