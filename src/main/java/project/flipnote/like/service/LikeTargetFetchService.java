package project.flipnote.like.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.like.exception.LikeErrorCode;
import project.flipnote.like.model.LikeTargetResponse;
import project.flipnote.like.service.fetcher.LikeTargetFetcher;

@RequiredArgsConstructor
@Service
public class LikeTargetFetchService<T extends LikeTargetResponse> {

	private final List<LikeTargetFetcher<T>> fetchers;

	private Map<LikeTargetType, LikeTargetFetcher<T>> fetcherMap;

	@PostConstruct
	public void init() {
		this.fetcherMap = this.fetchers.stream()
			.collect(Collectors.toMap(LikeTargetFetcher::getTargetType, Function.identity()));
	}

	public boolean isTargetViewable(LikeTargetType targetType, Long targetId, Long userId) {
		LikeTargetFetcher<T> targetFetcher = getFetcher(targetType);

		return targetFetcher.isTargetViewable(targetId, userId);
	}

	public Map<Long, T> fetchByTypeAndIds(
		LikeTargetType targetType,
		Set<Long> targetIds,
		Long userId
	) {
		LikeTargetFetcher<T> targetFetcher = getFetcher(targetType);

		return targetFetcher.fetchByIds(targetIds, userId);
	}

	private LikeTargetFetcher<T> getFetcher(LikeTargetType targetType) {
		LikeTargetFetcher<T> fetcher = fetcherMap.get(targetType);
		if (fetcher == null) {
			throw new BizException(LikeErrorCode.INVALID_LIKE_TYPE);
		}

		return fetcher;
	}
}
