package project.flipnote.like.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.event.LikeEvent;
import project.flipnote.common.model.event.UnlikeEvent;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.like.entity.Like;
import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.like.exception.LikeErrorCode;
import project.flipnote.like.model.request.LikeSearchRequest;
import project.flipnote.like.model.response.LikeResponse;
import project.flipnote.like.model.response.LikeTargetResponse;
import project.flipnote.like.repository.LikeRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LikeService {

	private final LikeRepository likeRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final LikePolicyService likePolicyService;
	private final LikeTargetFetchService<LikeTargetResponse> likeTargetFetchService;
	private final LikeReader likeReader;

	/**
	 * 좋아요 추가
	 *
	 * @param userId   좋아요 누른 회원 ID
	 * @param targetType 좋아요 대상 타입
	 * @param targetId 좋아요 대상 ID
	 * @author 윤정환
	 */
	@Transactional
	public void addLike(Long userId, LikeTargetType targetType, Long targetId) {
		likePolicyService.validateTargetExists(targetType, targetId, userId);
		likePolicyService.validateNotAlreadyLiked(targetType, targetId, userId);

		Like like = Like.builder()
			.targetType(targetType)
			.targetId(targetId)
			.userId(userId)
			.build();

		try {
			likeRepository.save(like);
		} catch (DataIntegrityViolationException e) {
			throw new BizException(LikeErrorCode.ALREADY_LIKED);
		}

		eventPublisher.publishEvent(new LikeEvent(targetType.toEventType(), targetId, userId));
	}

	/**
	 * 좋아요 취소
	 *
	 * @param userId   좋아요 취소 누른 회원 ID
	 * @param targetType 좋아요 취소 대상 타입
	 * @param targetId 좋아요 취소 대상 ID
	 * @author 윤정환
	 */
	@Transactional
	public void removeLike(Long userId, LikeTargetType targetType, Long targetId) {
		Like like = likeReader.findByTargetAndUserId(targetType, targetId, userId);

		likeRepository.delete(like);

		eventPublisher.publishEvent(new UnlikeEvent(targetType.toEventType(), targetId, userId));
	}

	/**
	 * 좋아요 누른 목록을 페이징하여 조회합니다.
	 *
	 * @param userId   좋아요 누른 목록을 조회하는 회원의 ID
	 * @param targetType 조회할 좋아요 대상 타입
	 * @param req      페이징 및 검색 조건이 포함된 요청 정보
	 * @return 페이징된 좋아요 누른 목록
	 * @author 윤정환
	 */
	public PagingResponse<LikeResponse<LikeTargetResponse>> getLikes(
		Long userId,
		LikeTargetType targetType,
		LikeSearchRequest req
	) {
		Page<Like> likePage = likeRepository.findByTargetTypeAndUserId(targetType, userId, req.getPageRequest());
		Map<Long, LocalDateTime> likedAtMap = likePage.stream()
			.collect(Collectors.toMap(Like::getTargetId, Like::getCreatedAt));
		Set<Long> targetIds = likedAtMap.keySet();

		Map<Long, LikeTargetResponse> targetMap
			= likeTargetFetchService.fetchByTypeAndIds(targetType, targetIds, userId);
		Page<LikeResponse<LikeTargetResponse>> content = likePage
			.map(like -> new LikeResponse<>(targetMap.get(like.getTargetId()), likedAtMap.get(like.getTargetId())));

		return PagingResponse.from(content);
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
