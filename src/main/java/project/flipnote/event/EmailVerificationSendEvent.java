package project.flipnote.event;

public record EmailVerificationSendEvent(
	String to,
	String code,
	int ttl
) {
}
