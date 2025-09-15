package project.flipnote.bookmark.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.exception.BookmarkErrorCode;
import project.flipnote.bookmark.model.BookmarkTargetResponse;
import project.flipnote.bookmark.service.fetcher.BookmarkTargetFetcher;
import project.flipnote.common.exception.BizException;

@RequiredArgsConstructor
@Service
public class BookmarkTargetFetchService<T extends BookmarkTargetResponse> {

	private final List<BookmarkTargetFetcher<T>> fetchers;

	private Map<BookmarkTargetType, BookmarkTargetFetcher<T>> fetcherMap;

	@PostConstruct
	public void init() {
		this.fetcherMap = this.fetchers.stream()
			.collect(Collectors.toMap(BookmarkTargetFetcher::getTargetType, Function.identity()));
	}

	public boolean isTargetViewable(BookmarkTargetType targetType, Long targetId, Long userId) {
		BookmarkTargetFetcher<T> targetFetcher = getFetcher(targetType);

		return targetFetcher.isTargetViewable(targetId, userId);
	}

	public Map<Long, T> fetchByTypeAndIds(
		BookmarkTargetType targetType,
		Set<Long> targetIds,
		Long userId
	) {
		BookmarkTargetFetcher<T> targetFetcher = getFetcher(targetType);

		return targetFetcher.fetchByIds(targetIds, userId);
	}

	private BookmarkTargetFetcher<T> getFetcher(BookmarkTargetType targetType) {
		BookmarkTargetFetcher<T> fetcher = fetcherMap.get(targetType);
		if (fetcher == null) {
			throw new BizException(BookmarkErrorCode.BOOKMARK_FETCHER_NOT_FOUND);
		}

		return fetcher;
	}
}
