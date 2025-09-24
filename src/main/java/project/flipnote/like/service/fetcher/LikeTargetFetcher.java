package project.flipnote.like.service.fetcher;

import java.util.Map;
import java.util.Set;

import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.like.model.LikeTargetResponse;

public interface LikeTargetFetcher<T extends LikeTargetResponse> {
	LikeTargetType getTargetType();

	boolean isTargetViewable(Long targetId, Long userId);

	Map<Long, T> fetchByIds(Set<Long> targetIds, Long userId);
}
