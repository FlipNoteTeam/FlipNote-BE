package project.flipnote.like.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.service.CardSetService;
import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.common.exception.BizException;
import project.flipnote.like.exception.LikeErrorCode;
import project.flipnote.like.repository.LikeRepository;

@RequiredArgsConstructor
@Service
public class LikePolicyService {

	private final CardSetService cardSetService;
	private final LikeRepository likeRepository;

	public void validateTargetExists(LikeTargetType targetType, Long targetId) {
		boolean targetExists = false;
		switch (targetType) {
			case CARD_SET -> targetExists = cardSetService.existsById(targetId);
		}

		if (!targetExists) {
			throw new BizException(LikeErrorCode.LIKE_TARGET_NOT_FOUND);
		}
	}

	public void validateNotAlreadyLiked(LikeTargetType targetType, Long targetId, Long userId) {
		if (likeRepository.existsByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId)) {
			throw new BizException(LikeErrorCode.ALREADY_LIKED);
		}
	}
}
