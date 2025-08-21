package project.flipnote.common.model.event;

public record GroupInvitationCreatedEvent(
	Long groupId,
	Long inviteeId
) {
}
