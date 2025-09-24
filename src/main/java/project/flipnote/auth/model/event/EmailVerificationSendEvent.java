package project.flipnote.auth.model.event;

public record EmailVerificationSendEvent(
	String to,
	String code
) {
}
