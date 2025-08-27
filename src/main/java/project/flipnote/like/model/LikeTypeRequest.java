package project.flipnote.like.model;

import project.flipnote.common.entity.LikeType;

public enum LikeTypeRequest {
	card_sets;

	public LikeType toDomain() {
		switch (this) {
			case card_sets: return LikeType.CARD_SET;
			default: throw new IllegalArgumentException("Invalid LikeTypeRequest");
		}
	}
}
