package project.flipnote.common.model.event;

public record UserRegisteredEvent(
	String email,
	Long userId
) {
}
