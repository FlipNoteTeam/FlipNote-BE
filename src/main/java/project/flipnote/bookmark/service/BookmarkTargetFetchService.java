package project.flipnote.bookmark.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.exception.BookmarkErrorCode;
import project.flipnote.bookmark.service.fetcher.BookmarkTargetFetcher;
import project.flipnote.common.exception.BizException;

@RequiredArgsConstructor
@Service
public class BookmarkTargetFetchService {

	private final List<BookmarkTargetFetcher> fetchers;

	private Map<BookmarkTargetType, BookmarkTargetFetcher> fetcherMap;

	@PostConstruct
	public void init() {
		this.fetcherMap = this.fetchers.stream()
			.collect(Collectors.toMap(BookmarkTargetFetcher::getTargetType, Function.identity()));
	}

	public boolean existsByTypeAndId(BookmarkTargetType targetType, Long targetId) {
		BookmarkTargetFetcher targetFetcher = fetcherMap.get(targetType);
		if (targetFetcher == null) {
			throw new BizException(BookmarkErrorCode.BOOKMARK_FETCHER_NOT_FOUND);
		}

		return targetFetcher.existsById(targetId);
	}
}
