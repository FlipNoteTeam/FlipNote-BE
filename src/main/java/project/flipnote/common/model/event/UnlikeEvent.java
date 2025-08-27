package project.flipnote.common.model.event;

import project.flipnote.common.entity.LikeType;

public record UnlikeEvent(
	LikeType likeType,
	Long targetId,
	Long userId
) {
}
