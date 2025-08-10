package project.flipnote.group.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.group.entity.GroupGuestInvitation;
import project.flipnote.group.entity.GroupMemberInvitation;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.exception.GroupInvitationErrorCode;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.repository.GroupGuestInvitationRepository;
import project.flipnote.group.repository.GroupMemberInvitationRepository;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.service.UserService;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupInvitationService {

	private final UserService userService;
	private final GroupMemberInvitationRepository memberInvitationRepository;
	private final GroupGuestInvitationRepository guestInvitationRepository;
	private final GroupService groupService;

	/**
	 * 그룹에 회원 혹은 비회원 초대
	 * @param inviterUserId 초대한 회원 id
	 * @param groupId 초대한 그룹 id
	 * @param req 초대 대상 정보
	 * @author 윤정환
	 */
	@Transactional
	public void createGroupInvitation(Long inviterUserId, Long groupId, GroupInvitationCreateRequest req) {
		if (!groupService.hasPermission(groupId, inviterUserId, GroupPermissionStatus.INVITE)) {
			throw new BizException(GroupInvitationErrorCode.NO_INVITATION_PERMISSION);
		}

		String inviteeEmail = req.email();

		userService.findActiveUserByEmail(inviteeEmail).ifPresentOrElse(
			inviteeUser -> handleMemberInvitation(inviterUserId, groupId, inviteeUser),
			() -> handleGuestInvitation(inviterUserId, groupId, inviteeEmail)
		);
	}

	/**
	 * 그룹에 회원 초대
	 * @param inviterUserId 초대한 회원 id
	 * @param groupId 초대한 그룹 id
	 * @param inviteeUser 초대 받는 user
	 * @author 윤정환
	 */
	private void handleMemberInvitation(Long inviterUserId, Long groupId, UserProfile inviteeUser) {
		if (memberInvitationRepository.existsByGroupIdAndInviteeUserId(groupId, inviteeUser.getId())) {
			throw new BizException(GroupInvitationErrorCode.ALREADY_INVITED);
		}

		GroupMemberInvitation invitation = GroupMemberInvitation.builder()
			.groupId(groupId)
			.inviterUserId(inviterUserId)
			.inviteeUserId(inviteeUser.getId())
			.build();
		memberInvitationRepository.save(invitation);

		// TODO: 초대받은 회원한테 알림 전송
	}

	/**
	 * 그룹에 비회원 초대
	 * @param inviterUserId 초대한 회원 id
	 * @param groupId 초대한 그룹 id
	 * @param inviteeEmail 초대 받는 비회원 email
	 * @author 윤정환
	 */
	private void handleGuestInvitation(Long inviterUserId, Long groupId, String inviteeEmail) {
		if (guestInvitationRepository.existsByGroupIdAndInviteeEmail(groupId, inviteeEmail)) {
			throw new BizException(GroupInvitationErrorCode.ALREADY_INVITED);
		}

		GroupGuestInvitation invitation = GroupGuestInvitation.builder()
			.groupId(groupId)
			.inviterUserId(inviterUserId)
			.inviteeEmail(inviteeEmail)
			.build();
		guestInvitationRepository.save(invitation);

		// TODO: 초대받은 비회원한테 이메일 전송
	}
}
