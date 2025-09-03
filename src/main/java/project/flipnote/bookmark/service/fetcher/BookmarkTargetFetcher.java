package project.flipnote.bookmark.service.fetcher;

import project.flipnote.bookmark.entity.BookmarkTargetType;

public interface BookmarkTargetFetcher {
	BookmarkTargetType getTargetType();

	boolean existsById(Long targetId);
}
