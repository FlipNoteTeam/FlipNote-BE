package project.flipnote.like.model;

import project.flipnote.like.entity.LikeTargetType;

public enum LikeTargetTypeRequest {
	card_set;

	public LikeTargetType toDomainType() {
		switch (this) {
			case card_set: return LikeTargetType.CARD_SET;
			default: throw new IllegalArgumentException("Invalid LikeTargetTypeRequest");
		}
	}
}
