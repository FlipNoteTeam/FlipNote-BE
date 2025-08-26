package project.flipnote.group.model.event;

public record GuestGroupInvitationCreateEvent(
	String email,
	String groupName
) {
}
