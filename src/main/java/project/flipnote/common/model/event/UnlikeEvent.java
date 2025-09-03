package project.flipnote.common.model.event;

public record UnlikeEvent(
	LikeEventTargetType targetType,
	Long targetId,
	Long userId
) {
}
