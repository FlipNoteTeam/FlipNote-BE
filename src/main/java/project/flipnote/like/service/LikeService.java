package project.flipnote.like.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.entity.LikeType;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.event.LikeEvent;
import project.flipnote.common.model.event.UnlikeEvent;
import project.flipnote.like.entity.Like;
import project.flipnote.like.exception.LikeErrorCode;
import project.flipnote.like.repository.LikeRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LikeService {

	private final LikeRepository likeRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final LikePolicyService likePolicyService;

	/**
	 * 좋아요 추가
	 *
	 * @param userId   좋아요 누른 회원 ID
	 * @param likeType 좋아요 대상 타입
	 * @param targetId 좋아요 대상 ID
	 * @author 윤정환
	 */
	@Transactional
	public void addLike(Long userId, LikeType likeType, Long targetId) {
		likePolicyService.validateTargetExists(likeType, targetId);
		likePolicyService.validateNotAlreadyLiked(likeType, targetId, userId);

		Like like = Like.builder()
			.type(likeType)
			.targetId(targetId)
			.userId(userId)
			.build();
		likeRepository.save(like);

		eventPublisher.publishEvent(new LikeEvent(likeType, targetId, userId));
	}

	/**
	 * 좋아요 취소
	 *
	 * @param userId   좋아요 취소 누른 회원 ID
	 * @param likeType 좋아요 취소 대상 타입
	 * @param targetId 좋아요 취소 대상 ID
	 * @author 윤정환
	 */
	@Transactional
	public void removeLike(Long userId, LikeType likeType, Long targetId) {
		Like like = likeRepository.findByTypeAndTargetIdAndUserId(likeType, targetId, userId)
			.orElseThrow(() -> new BizException(LikeErrorCode.LIKE_NOT_FOUND));

		likeRepository.delete(like);

		eventPublisher.publishEvent(new UnlikeEvent(likeType, targetId, userId));
	}
}
