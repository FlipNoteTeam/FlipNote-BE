package project.flipnote.group.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.group.entity.GroupInvitation;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.exception.GroupInvitationErrorCode;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.repository.GroupInvitationRepository;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.service.UserService;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupInvitationService {

	private final UserService userService;
	private final GroupInvitationRepository groupInvitationRepository;
	private final GroupService groupService;

	/**
	 * 그룹에 회원 혹은 비회원 초대
	 *
	 * @param inviterUserId 초대한 회원 id
	 * @param groupId       초대한 그룹 id
	 * @param req           초대 대상 정보
	 * @author 윤정환
	 */
	@Transactional
	public void createGroupInvitation(Long inviterUserId, Long groupId, GroupInvitationCreateRequest req) {
		validateGroupInvitePermission(inviterUserId, groupId);

		String inviteeEmail = req.email();

		userService.findActiveUserByEmail(inviteeEmail).ifPresentOrElse(
			inviteeUser -> createUserInvitation(inviterUserId, groupId, inviteeUser),
			() -> createGuestInvitation(inviterUserId, groupId, inviteeEmail)
		);
	}

	/**
	 * 그룹 초대 권한을 검증
	 *
	 * @param userId  권한을 검증할 사용자 ID
	 * @param groupId 검증할 그룹 ID
	 * @author 윤정환
	 */
	private void validateGroupInvitePermission(Long userId, Long groupId) {
		if (!groupService.hasPermission(groupId, userId, GroupPermissionStatus.INVITE)) {
			throw new BizException(GroupInvitationErrorCode.NO_INVITATION_PERMISSION);
		}
	}

	/**
	 * 그룹에 회원 초대
	 *
	 * @param inviterUserId 초대한 회원 id
	 * @param groupId       초대한 그룹 id
	 * @param inviteeUser   초대 받는 user
	 * @author 윤정환
	 */
	private void createUserInvitation(Long inviterUserId, Long groupId, UserProfile inviteeUser) {
		if (groupInvitationRepository.existsByGroupIdAndInviteeUserId(groupId, inviteeUser.getId())) {
			throw new BizException(GroupInvitationErrorCode.ALREADY_INVITED);
		}

		GroupInvitation invitation = GroupInvitation.builder()
			.groupId(groupId)
			.inviterUserId(inviterUserId)
			.inviteeUserId(inviteeUser.getId())
			.build();
		groupInvitationRepository.save(invitation);

		// TODO: 초대받은 회원한테 알림 전송
	}

	/**
	 * 그룹에 비회원 초대
	 *
	 * @param inviterUserId 초대한 회원 id
	 * @param groupId       초대한 그룹 id
	 * @param inviteeEmail  초대 받는 비회원 email
	 * @author 윤정환
	 */
	private void createGuestInvitation(Long inviterUserId, Long groupId, String inviteeEmail) {
		if (groupInvitationRepository.existsByGroupIdAndInviteeEmail(groupId, inviteeEmail)) {
			throw new BizException(GroupInvitationErrorCode.ALREADY_INVITED);
		}

		GroupInvitation invitation = GroupInvitation.builder()
			.groupId(groupId)
			.inviterUserId(inviterUserId)
			.inviteeEmail(inviteeEmail)
			.build();
		groupInvitationRepository.save(invitation);

		// TODO: 초대받은 비회원한테 이메일 전송
	}
}
