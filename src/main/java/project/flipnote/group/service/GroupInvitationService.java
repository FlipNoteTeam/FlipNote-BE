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
	}

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
	}
}
