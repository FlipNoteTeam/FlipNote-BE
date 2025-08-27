package project.flipnote.like.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.service.CardSetService;
import project.flipnote.common.entity.LikeType;
import project.flipnote.common.exception.BizException;
import project.flipnote.like.exception.LikeErrorCode;
import project.flipnote.like.repository.LikeRepository;

@RequiredArgsConstructor
@Service
public class LikePolicyService {

	private final CardSetService cardSetService;
	private final LikeRepository likeRepository;

	public void validateTargetExists(LikeType likeType, Long targetId) {
		boolean targetExists = false;
		switch (likeType) {
			case CARD_SET -> targetExists = cardSetService.existsById(targetId);
		}

		if (!targetExists) {
			throw new BizException(LikeErrorCode.LIKE_TARGET_NOT_FOUND);
		}
	}

	public void validateNotAlreadyLiked(LikeType likeType, Long targetId, Long userId) {
		if (likeRepository.existsByTypeAndTargetIdAndUserId(likeType, targetId, userId)) {
			throw new BizException(LikeErrorCode.ALREADY_LIKED);
		}
	}
}
