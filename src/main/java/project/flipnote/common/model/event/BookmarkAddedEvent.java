package project.flipnote.common.model.event;

public record BookmarkAddedEvent(
	BookmarkEventTargetType targetType,
	Long targetId,
	Long userId
) {
}
