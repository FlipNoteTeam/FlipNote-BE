package project.flipnote.like.service.fetcher;

import java.util.List;
import java.util.Map;

import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.like.model.LikeTargetResponse;

public interface LikeTargetFetcher<T extends LikeTargetResponse> {
	LikeTargetType getTargetType();

	Map<Long, T> fetchByIds(List<Long> ids);
}
