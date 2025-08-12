package project.flipnote.group.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import project.flipnote.group.entity.GroupInvitation;

public record IncomingGroupInvitationResponse(
	Long invitationId,
	Long groupId,
	GroupInvitationStatus status,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt
) {

	public static IncomingGroupInvitationResponse from(GroupInvitation invitation) {
		return new IncomingGroupInvitationResponse(
			invitation.getId(),
			invitation.getGroup().getId(),
			GroupInvitationStatus.from(invitation.getStatus()),
			invitation.getCreatedAt()
		);
	}
}
