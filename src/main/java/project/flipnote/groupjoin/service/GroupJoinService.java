package project.flipnote.groupjoin.service;


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
import project.flipnote.groupjoin.entity.GroupJoin;
import project.flipnote.groupjoin.entity.GroupJoinStatus;
import project.flipnote.groupjoin.exception.GroupJoinErrorCode;
import project.flipnote.groupjoin.model.*;
import project.flipnote.groupjoin.repository.GroupJoinRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupJoinService {
	private final GroupRepository groupRepository;
	private final UserRepository userRepository;
	private final GroupJoinRepository groupJoinRepository;
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
				() -> new BizException(GroupJoinErrorCode.USER_NOT_IN_GROUP)
		);

		GroupPermission groupPermission = groupPermissionRepository.findByName(GroupPermissionStatus.JOIN_REQUEST_MANAGE);

        return groupRolePermissionRepository.existByGroupAndRoleAndGroupPermission(
				group,
				groupMember.getRole(),
				groupPermission);
	}
	
	private List<GroupJoin> findGroupJoins(Group group) {
        return groupJoinRepository.findAllByGroup(group);
	}

	private GroupJoin findGroupApplication(Long joinId) {
		return groupJoinRepository.findById(joinId).orElseThrow(
				() ->  new BizException(GroupJoinErrorCode.NOT_EXIST_JOIN)
		);
	}
	
	//가입 신청 요청
	@Transactional
	public GroupJoinResponse joinRequest(UserAuth userAuth, Long groupId, GroupJoinRequest req) {
		//유저 조회
		User user = findUser(userAuth);
		//그룹 조회
		Group group = findGroup(groupId);

		GroupJoin groupJoin = GroupJoin.builder()
				.group(group)
				.user(user)
				.joinIntro(req.joinIntro())
				.status(GroupJoinStatus.PENDING)
				.build();

		groupJoinRepository.save(groupJoin);

		return GroupJoinResponse.from(groupJoin.getId());
	}

	//그룹 가입 신청 리스트 조회
	public GroupJoinListResponse findGroupJoinList(UserAuth userAuth, Long groupId) {
		//유저 조회
		User user = findUser(userAuth);

		//그룹 조회
		Group group = findGroup(groupId);
		
		//그룹 내 권한 조회
		Boolean isExistPermission = hasPermission(group, user);
		
		//권한 존재하지 않으면 에러
		if (!isExistPermission) {
			throw new BizException(GroupJoinErrorCode.USER_NOT_PERMISSION);
		}

		//그룹 내 가입 신청 리스트 조회
		List<GroupJoin> groupJoins = findGroupJoins(group);
		
		//반환
		return GroupJoinListResponse.from(groupJoins);
	}

	//가입 신청 응답
	@Transactional
	public GroupJoinRespondResponse respondToJoinRequest(UserAuth userAuth, Long groupId, Long joinId, @Valid GroupJoinRespondRequest req) {
		//유저 조회
		User user = findUser(userAuth);

		//그룹 조회
		Group group = findGroup(groupId);

		//그룹 내 권한 조회
		Boolean isExistPermission = hasPermission(group, user);

		//권한 존재하지 않으면 에러
		if (!isExistPermission) {
			throw new BizException(GroupJoinErrorCode.USER_NOT_PERMISSION);
		}

		//그룹 가입 신청 조회
		GroupJoin groupJoin = findGroupApplication(joinId);
		
		groupJoin.updateStatus(req.status());

		groupJoinRepository.save(groupJoin);

		return GroupJoinRespondResponse.from(groupJoin.getId());
	}

	@Transactional
	public void groupJoinDelete(UserAuth userAuth, Long groupId, Long joinId) {
		//유저 조회
		User user = findUser(userAuth);

		//신청 조회
		GroupJoin groupJoin = groupJoinRepository.findById(joinId).orElseThrow(
			() -> new BizException(GroupJoinErrorCode.NOT_EXIST_JOIN)
		);

		//그룹이 일치하지 않으면 에러
		if (!groupJoin.getGroup().getId().equals(groupId)) {
			throw new BizException(GroupJoinErrorCode.USER_NOT_PERMISSION);
		}

		//자신이 유저가 아니면 에러
		if (!groupJoin.getUser().getId().equals(user.getId())) {
			throw new BizException(GroupJoinErrorCode.USER_NOT_PERMISSION);
		}

		//삭제
		groupJoinRepository.deleteById(joinId);
	}

	public FIndGroupJoinListMeResponse findGroupJoinListMe(UserAuth userAuth) {
		//유저 조회
		User user = findUser(userAuth);

		//유저별 그룹 신청 리스트 조회
		List<GroupJoin> groupJoins = groupJoinRepository.findAllByUser(user);

		return FIndGroupJoinListMeResponse.from(groupJoins);
	}
}
