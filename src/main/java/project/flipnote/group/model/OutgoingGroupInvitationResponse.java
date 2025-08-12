package project.flipnote.group.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import project.flipnote.group.entity.GroupInvitation;

public record OutgoingGroupInvitationResponse(
	Long invitationId,
	Long inviterUserId,
	Long inviteeUserId,
	String inviteeEmail,
	String inviteeNickname,
	GroupInvitationStatus status,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt
) {

	public static OutgoingGroupInvitationResponse from(GroupInvitation invitation, String inviteeNickname) {
		return new OutgoingGroupInvitationResponse(
			invitation.getId(),
			invitation.getInviterUserId(),
			invitation.getInviteeUserId(),
			invitation.getInviteeEmail(),
			inviteeNickname,
			GroupInvitationStatus.from(invitation.getStatus()),
			invitation.getCreatedAt()
		);
	}
}
