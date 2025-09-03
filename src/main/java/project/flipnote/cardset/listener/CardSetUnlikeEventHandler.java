package project.flipnote.cardset.listener;

import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.cardset.service.CardSetService;
import project.flipnote.common.entity.LikeType;
import project.flipnote.common.model.event.UnlikeEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class CardSetUnlikeEventHandler {

	private final CardSetService cardSetService;

	@Async
	@Retryable(
		maxAttempts = 3,
		retryFor = DataAccessException.class,
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleUnlikeEvent(UnlikeEvent event) {
		if (event.likeType() != LikeType.CARD_SET) {
			return;
		}

		cardSetService.decrementLikeCount(event.targetId());
	}

	@Recover
	public void recover(Exception ex, UnlikeEvent event) {
		log.error(
			"좋아요 취소 수 반영 처리 중 예외 발생 : likeType={}, targetId={}, userId={}",
			event.likeType(), event.targetId(), event.userId(), ex
		);
	}
}
