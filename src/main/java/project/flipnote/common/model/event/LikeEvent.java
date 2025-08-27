package project.flipnote.common.model.event;

import project.flipnote.common.entity.LikeType;

public record LikeEvent(
	LikeType likeType,
	Long targetId,
	Long userId
) {
}
