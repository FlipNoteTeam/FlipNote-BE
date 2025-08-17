package project.flipnote.groupjoin.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.event.GroupJoinRequestedEvent;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.entity.GroupPermission;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.entity.GroupRolePermission;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupPermissionRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.group.repository.GroupRolePermissionRepository;
import project.flipnote.groupjoin.entity.GroupJoin;
import project.flipnote.groupjoin.entity.GroupJoinStatus;
import project.flipnote.groupjoin.exception.GroupJoinErrorCode;
import project.flipnote.groupjoin.model.FindGroupJoinListMeResponse;
import project.flipnote.groupjoin.model.GroupJoinListResponse;
import project.flipnote.groupjoin.model.GroupJoinRequest;
import project.flipnote.groupjoin.model.GroupJoinRespondRequest;
import project.flipnote.groupjoin.model.GroupJoinRespondResponse;
import project.flipnote.groupjoin.model.GroupJoinResponse;
import project.flipnote.groupjoin.repository.GroupJoinRepository;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserProfileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupJoinService {
	private final GroupRepository groupRepository;
	private final UserProfileRepository userProfileRepository;
	private final GroupJoinRepository groupJoinRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final GroupRolePermissionRepository groupRolePermissionRepository;
	private final GroupPermissionRepository groupPermissionRepository;
	private final ApplicationEventPublisher eventPublisher;

	//유저 정보 조회
	private UserProfile findUser(AuthPrinciple authPrinciple) {
		return userProfileRepository.findByIdAndStatus(authPrinciple.userId(), UserStatus.ACTIVE).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	//그룹 정보 조회
	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(
			() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND)
		);
	}

	//중복조회
	private Boolean existGroupJoin(Group group, UserProfile userProfile) {
		return groupJoinRepository.existsByGroup_idAndUser_id(group.getId(), userProfile.getId());
	}

	private void checkMaxMember(Group group) {

		Group lockedGroup = groupRepository.findByIdForUpdate(group.getId()).orElseThrow(
			() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND)
		);

		long currentMemberCount = groupMemberRepository.countByGroup_Id(lockedGroup.getId());
		if (currentMemberCount >= lockedGroup.getMaxMember()) {
			throw new BizException(GroupJoinErrorCode.GROUP_IS_ALREADY_MAX_MEMBER);
		}
	}

	//그룹 내 권한 정보 조회
	private Boolean hasPermission(Group group, UserProfile userProfile) {
		GroupMember groupMember = groupMemberRepository.findByGroupAndUser(group, userProfile).orElseThrow(
			() -> new BizException(GroupJoinErrorCode.USER_NOT_IN_GROUP)
		);

		GroupPermission groupPermission = groupPermissionRepository.findByName(
			GroupPermissionStatus.JOIN_REQUEST_MANAGE);

		return groupRolePermissionRepository.existsByGroupAndRoleAndGroupPermission(
			group,
			groupMember.getRole(),
			groupPermission);
	}

	//그룹 내 모든 가입신청 요청 조회
	private List<GroupJoin> findGroupJoins(Group group) {
		return groupJoinRepository.findAllByGroup(group);
	}

	//가입 신청 조회
	private GroupJoin findGroupJoin(Long joinId) {
		return groupJoinRepository.findById(joinId).orElseThrow(
			() -> new BizException(GroupJoinErrorCode.NOT_EXIST_JOIN)
		);
	}

	//가입 신청 요청
	@Transactional
	public GroupJoinResponse joinRequest(AuthPrinciple authPrinciple, Long groupId, GroupJoinRequest req) {
		//유저 조회
		UserProfile user = findUser(authPrinciple);
		//그룹 조회
		Group group = findGroup(groupId);

		if (existGroupJoin(group, user)) {
			throw new BizException(GroupJoinErrorCode.ALREADY_JOINED_GROUP);
		}

		//비공개 그룹일 경우
		if (!group.getPublicVisible()) {
			throw new BizException(GroupJoinErrorCode.GROUP_IS_NOT_PUBLIC);
		}

		//그룹이 최대인원인 경우
		checkMaxMember(group);

		GroupJoinStatus status = GroupJoinStatus.ACCEPT;
		//가입 신청이 필수이면 pending 아니면 바로 가입
		if (group.getApplicationRequired()) {
			status = GroupJoinStatus.PENDING;
		}

		GroupJoin groupJoin = GroupJoin.builder()
			.group(group)
			.user(user)
			.joinIntro(req.joinIntro())
			.status(status)
			.build();

		groupJoinRepository.save(groupJoin);

		if (group.getApplicationRequired()) {
			sendJoinRequestNotification(group, user);
		}

		return GroupJoinResponse.from(groupJoin.getId(), groupJoin.getStatus());
	}

	/**
	 * 그룹 가입 신청 알림 전송
	 *
	 * @param group     가입 신청 대상 그룹
	 * @param requester 가입 신청자
	 * @author 윤정환
	 */
	private void sendJoinRequestNotification(Group group, UserProfile requester) {
		// TODO: GroupPermission 캐시 혹은 name 자체를 pk로 써서 쿼리 조회 없이 사용할 수 있도록 수정 필요
		GroupPermission groupPermission = groupPermissionRepository
			.findByName(GroupPermissionStatus.JOIN_REQUEST_MANAGE);
		List<GroupMemberRole> memberRoles = groupRolePermissionRepository
			.findByGroupAndGroupPermission(group, groupPermission)
			.stream()
			.map(GroupRolePermission::getRole)
			.toList();
		List<Long> receiverIds = groupMemberRepository.findByGroupAndRoleIn(group, memberRoles)
			.stream()
			.map(GroupMember::getId)
			.toList();

		eventPublisher.publishEvent(new GroupJoinRequestedEvent(group.getId(), receiverIds, requester.getId()));
	}

	//그룹 가입 신청 리스트 조회
	public GroupJoinListResponse findGroupJoinList(AuthPrinciple authPrinciple, Long groupId) {
		//유저 조회
		UserProfile userProfile = findUser(authPrinciple);

		//그룹 조회
		Group group = findGroup(groupId);

		//그룹 내 권한 조회
		Boolean isExistPermission = hasPermission(group, userProfile);

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
	public GroupJoinRespondResponse respondToJoinRequest(AuthPrinciple authPrinciple, Long groupId, Long joinId,
		@Valid GroupJoinRespondRequest req) {
		//유저 조회
		UserProfile user = findUser(authPrinciple);

		//그룹 조회
		Group group = findGroup(groupId);

		//그룹 내 권한 조회
		Boolean isExistPermission = hasPermission(group, user);

		//권한 존재하지 않으면 에러
		if (!isExistPermission) {
			throw new BizException(GroupJoinErrorCode.USER_NOT_PERMISSION);
		}

		//그룹 가입 신청 조회
		GroupJoin groupJoin = findGroupJoin(joinId);

		//최대 인원 조회
		if (req.status() == GroupJoinStatus.ACCEPT) {
			checkMaxMember(group);
			// 업데이트
			groupJoin.updateStatus(req.status());

			groupJoinRepository.save(groupJoin);

			//그룹 멤버 추가
			GroupMember groupMember = GroupMember.builder()
				.group(group)
				.user(groupJoin.getUser())
				.role(GroupMemberRole.MEMBER)
				.build();

			groupMemberRepository.save(groupMember);

			return GroupJoinRespondResponse.from(groupJoin.getId());
		}

		groupJoin.updateStatus(req.status());

		groupJoinRepository.save(groupJoin);

		return GroupJoinRespondResponse.from(groupJoin.getId());
	}

	//삭제
	@Transactional
	public void groupJoinDelete(AuthPrinciple authPrinciple, Long groupId, Long joinId) {
		//유저 조회
		UserProfile user = findUser(authPrinciple);

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

		groupJoin.updateStatus(GroupJoinStatus.CANCEL);

		//삭제
		groupJoinRepository.save(groupJoin);
	}

	//내가 신청한 리스트 조회
	public FindGroupJoinListMeResponse findGroupJoinListMe(AuthPrinciple authPrinciple) {
		//유저 조회
		UserProfile user = findUser(authPrinciple);

		//유저별 그룹 신청 리스트 조회
		List<GroupJoin> groupJoins = groupJoinRepository.findAllByUser(user);

		return FindGroupJoinListMeResponse.from(groupJoins);
	}
}
