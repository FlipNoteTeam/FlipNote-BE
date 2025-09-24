package project.flipnote.like.model.request;

import project.flipnote.like.entity.LikeTargetType;

public enum LikeTargetTypeRequest {
	card_set;

	public LikeTargetType toDomainType() {
		return switch (this) {
			case card_set -> LikeTargetType.CARD_SET;
		};
	}
}
