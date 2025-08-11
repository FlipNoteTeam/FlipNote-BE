package project.flipnote.group.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import project.flipnote.group.entity.GroupInvitation;

public record GroupInvitationResponse(
	Long invitationId,
	Long inviterUserId,
	Long inviteeUserId,
	String inviteeEmail,
	String inviteeNickname,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt
) {

	public static GroupInvitationResponse from(GroupInvitation invitation, String inviteeNickname) {
		return new GroupInvitationResponse(
			invitation.getId(),
			invitation.getInviterUserId(),
			invitation.getInviteeUserId(),
			invitation.getInviteeEmail(),
			inviteeNickname,
			invitation.getCreatedAt()
		);
	}
}
