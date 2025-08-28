package project.flipnote.like.service.fetcher;

import java.util.List;

import project.flipnote.common.entity.LikeType;
import project.flipnote.like.model.LikeTargetResponse;

public interface LikeTargetFetcher<T extends LikeTargetResponse> {
	LikeType getLikeType();
	List<T> fetchByIds(List<Long> ids);
}
