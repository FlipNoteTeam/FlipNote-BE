package project.flipnote.like.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.exception.BookmarkErrorCode;
import project.flipnote.common.exception.BizException;
import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.like.exception.LikeErrorCode;
import project.flipnote.like.model.response.LikeTargetResponse;
import project.flipnote.like.repository.LikeRepository;

@RequiredArgsConstructor
@Service
public class LikePolicyService {

	private final LikeRepository likeRepository;
	private final LikeTargetFetchService<LikeTargetResponse> likeTargetFetchService;

	public void validateTargetExists(LikeTargetType targetType, Long targetId, Long userId) {
		if (!likeTargetFetchService.isTargetViewable(targetType, targetId, userId)) {
			throw new BizException(BookmarkErrorCode.BOOKMARK_TARGET_NOT_FOUND);
		}
	}

	public void validateNotAlreadyLiked(LikeTargetType targetType, Long targetId, Long userId) {
		if (likeRepository.existsByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId)) {
			throw new BizException(LikeErrorCode.ALREADY_LIKED);
		}
	}
}
