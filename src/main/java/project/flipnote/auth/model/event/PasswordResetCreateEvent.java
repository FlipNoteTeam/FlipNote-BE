package project.flipnote.auth.model.event;

public record PasswordResetCreateEvent(
	String to,
	String link
) {
}
