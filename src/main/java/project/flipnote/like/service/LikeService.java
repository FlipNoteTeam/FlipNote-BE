package project.flipnote.like.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.entity.LikeType;
import project.flipnote.common.model.event.LikeEvent;
import project.flipnote.like.entity.Like;
import project.flipnote.like.repository.LikeRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeService {

	private final LikeRepository likeRepository;
	private final ApplicationEventPublisher eventPublisher;
	private final LikePolicyService likePolicyService;

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
}
