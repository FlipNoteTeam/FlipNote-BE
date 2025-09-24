package project.flipnote.like.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.like.entity.Like;
import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.like.exception.LikeErrorCode;
import project.flipnote.like.repository.LikeRepository;

@RequiredArgsConstructor
@Service
public class LikeReader {

	private final LikeRepository likeRepository;

	public Like findByTargetAndUserId(LikeTargetType targetType, Long targetId, Long userId) {
		return likeRepository.findByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId)
			.orElseThrow(() -> new BizException(LikeErrorCode.LIKE_NOT_FOUND));
	}
}
