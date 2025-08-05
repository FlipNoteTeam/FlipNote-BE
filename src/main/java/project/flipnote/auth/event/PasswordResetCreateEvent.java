package project.flipnote.auth.event;

public record PasswordResetCreateEvent(
	String to,
	String link
) {
}
