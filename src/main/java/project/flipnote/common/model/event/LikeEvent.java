package project.flipnote.common.model.event;

public record LikeEvent(
	LikeEventTargetType targetType,
	Long targetId,
	Long userId
) {
}
