package project.flipnote.like.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.entity.LikeType;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.event.LikeEvent;
import project.flipnote.common.model.event.UnlikeEvent;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.like.entity.Like;
import project.flipnote.like.exception.LikeErrorCode;
import project.flipnote.like.model.LikeResponse;
import project.flipnote.like.model.LikeSearchRequest;
import project.flipnote.like.model.LikeTargetResponse;
import project.flipnote.like.repository.LikeRepository;
import project.flipnote.like.service.fetcher.LikeTargetFetcher;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LikeService {

	private final LikeRepository likeRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final LikePolicyService likePolicyService;
	private final List<LikeTargetFetcher<?>> fetchers;

	private Map<LikeType, LikeTargetFetcher<?>> fetcherMap;

	@PostConstruct
	public void init() {
		this.fetcherMap = this.fetchers.stream()
			.collect(Collectors.toMap(LikeTargetFetcher::getLikeType, Function.identity()));
	}

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

	/**
	 * 좋아요 누른 목록을 페이징하여 조회합니다.
	 *
	 * @param userId   좋아요 누른 목록을 조회하는 회원의 ID
	 * @param likeType 조회할 좋아요 대상 타입
	 * @param req      페이징 및 검색 조건이 포함된 요청 정보
	 * @param <T>      좋아요 대상의 상세 정보를 담은 DTO 타입 (LikeTargetResponse 하위 타입)
	 * @return 페이징된 좋아요 누른 목록
	 * @author 윤정환
	 */
	public <T extends LikeTargetResponse> PagingResponse<LikeResponse<T>> getLikes(
		Long userId,
		LikeType likeType,
		LikeSearchRequest req
	) {
		Page<Like> likePage = likeRepository.findByTypeAndUserId(likeType, userId, req.getPageRequest());
		Map<Long, LocalDateTime> likedAtMap = likePage.stream()
			.collect(Collectors.toMap(Like::getTargetId, Like::getCreatedAt));
		List<Long> targetIds = likePage.stream()
			.map(Like::getTargetId)
			.toList();

		// TODO: 제네릭이 아닌 타입 별로 엔드포인트를 따로 만드는게 좋으려나 고민중, 현재 방법을 유지하면서 더 나은 구조 알고싶음...
		LikeTargetFetcher<T> fetcher = (LikeTargetFetcher<T>)fetcherMap.get(likeType);
		if (fetcher == null) {
			throw new BizException(LikeErrorCode.INVALID_LIKE_TYPE);
		}

		List<T> targets = fetcher.fetchByIds(targetIds);
		Map<Long, T> targetMap = targets.stream()
			.collect(Collectors.toMap(LikeTargetResponse::getId, Function.identity()));

		Page<LikeResponse<T>> content = likePage
			.map(like -> new LikeResponse<>(targetMap.get(like.getTargetId()), likedAtMap.get(like.getTargetId())));

		return PagingResponse.from(content);
	}
}
