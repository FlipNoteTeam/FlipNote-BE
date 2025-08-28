package project.flipnote.like.model;

import project.flipnote.common.entity.LikeType;

public enum LikeTypeRequest {
	card_set;

	public LikeType toDomain() {
		switch (this) {
			case card_set: return LikeType.CARD_SET;
			default: throw new IllegalArgumentException("Invalid LikeTypeRequest");
		}
	}
}
