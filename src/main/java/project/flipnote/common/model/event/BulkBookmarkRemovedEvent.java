package project.flipnote.common.model.event;

import java.util.List;

public record BulkBookmarkRemovedEvent(
	BookmarkEventTargetType targetType,
	List<Long> targetIds,
	Long userId
) {
}
