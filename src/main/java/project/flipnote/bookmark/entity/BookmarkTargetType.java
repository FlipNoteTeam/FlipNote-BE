package project.flipnote.bookmark.entity;

import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.model.event.BookmarkEventTargetType;

@Slf4j
public enum BookmarkTargetType {
	CARD_SET;

	public BookmarkEventTargetType toEventType() {
		try {
			return BookmarkEventTargetType.valueOf(this.name());
		} catch (IllegalArgumentException e) {
			log.error("Failed to map BookmarkTargetType '{}' to BookmarkEventTargetType", this.name(), e);
			throw new IllegalStateException(
				"Invalid mapping from BookmarkTargetType to BookmarkEventTargetType: " + this.name(),
				e
			);
		}
	}
}
