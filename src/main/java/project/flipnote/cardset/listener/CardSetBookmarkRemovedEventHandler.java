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
import project.flipnote.common.model.event.BookmarkEventTargetType;
import project.flipnote.common.model.event.BookmarkRemovedEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class CardSetBookmarkRemovedEventHandler {

	private final CardSetService cardSetService;

	@Async
	@Retryable(
		maxAttempts = 3,
		retryFor = DataAccessException.class,
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleBookmarkRemovedEvent(BookmarkRemovedEvent event) {
		if (event.targetType() != BookmarkEventTargetType.CARD_SET) {
			return;
		}

		cardSetService.decrementBookmarkCount(event.targetId());
	}

	@Recover
	public void recover(Exception ex, BookmarkRemovedEvent event) {
		log.error(
			"카드셋 즐겨찾기 삭제 처리 중 예외 발생 : targetType={}, targetId={}, userId={}",
			event.targetType(), event.targetId(), event.userId(), ex
		);
	}
}
