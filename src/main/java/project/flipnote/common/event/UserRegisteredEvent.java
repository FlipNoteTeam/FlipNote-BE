package project.flipnote.common.event;

public record UserRegisteredEvent(
	Long userId,
	String email
) {
}
