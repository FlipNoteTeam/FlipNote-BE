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


	/**
	 * 좋아요 누른 여부를 조회합니다.
	 *
	 * @param userId      좋아요를 눌렀는지 확인할 회원 ID
	 * @param targetType  좋아요 대상의 타입
	 * @param targetId    좋아요 대상의 ID
	 * @return 타겟을 좋아요 눌렀으면 true, 아니면 false
	 * @author 윤정환
	 */
	public boolean isLiked(Long userId, LikeTargetType targetType, Long targetId) {
		return likeRepository.existsByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId);
	}
}
