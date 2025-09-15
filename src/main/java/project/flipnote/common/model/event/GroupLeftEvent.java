package project.flipnote.common.model.event;

public record GroupLeftEvent(
	Long groupId,
	Long userId
) {
}
