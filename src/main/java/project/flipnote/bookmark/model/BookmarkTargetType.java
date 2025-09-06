package project.flipnote.bookmark.model;

public enum BookmarkTargetType {
	card_sets;

	public project.flipnote.bookmark.entity.BookmarkTargetType toDomainType() {
		return switch (this) {
			case card_sets -> project.flipnote.bookmark.entity.BookmarkTargetType.CARD_SET;
		};
	}
}
