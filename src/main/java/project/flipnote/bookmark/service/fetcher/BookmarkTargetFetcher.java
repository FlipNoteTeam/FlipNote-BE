package project.flipnote.bookmark.service.fetcher;

import java.util.Map;
import java.util.Set;

import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.model.BookmarkTargetResponse;

public interface BookmarkTargetFetcher<T extends BookmarkTargetResponse> {
	BookmarkTargetType getTargetType();

	boolean isTargetViewable(Long targetId, Long userId);

	Map<Long, T> fetchByIds(Set<Long> ids);
}
