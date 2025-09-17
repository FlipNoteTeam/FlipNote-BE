package project.flipnote.common.model.event;

public record BookmarkRemovedEvent(
	BookmarkEventTargetType targetType,
	Long targetId,
	Long userId
) {
}
