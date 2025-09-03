package project.flipnote.bookmark.service.fetcher;

import java.util.Map;
import java.util.Set;

import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.model.BookmarkTargetResponse;

public interface BookmarkTargetFetcher<T extends BookmarkTargetResponse> {
	BookmarkTargetType getTargetType();

	boolean existsById(Long targetId);

	Map<Long, T> fetchByIds(Set<Long> ids);
}
