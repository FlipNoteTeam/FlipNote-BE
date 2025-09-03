package project.flipnote.like.entity;

import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.model.event.LikeEventTargetType;

@Slf4j
public enum LikeTargetType {
	CARD_SET;

	public LikeEventTargetType toEventType() {
		try {
			return LikeEventTargetType.valueOf(this.name());
		} catch (IllegalArgumentException e) {
			log.error("Failed to map LikeTargetType '{}' to LikeEventTargetType", this.name(), e);
			throw new IllegalStateException(
				"Invalid mapping from LikeTargetType to LikeEventTargetType: " + this.name(),
				e
			);
		}
	}
}
