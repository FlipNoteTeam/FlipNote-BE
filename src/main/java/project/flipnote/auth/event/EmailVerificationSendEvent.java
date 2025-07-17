package project.flipnote.auth.event;

public record EmailVerificationSendEvent(
	String to,
	String code
) {
}
