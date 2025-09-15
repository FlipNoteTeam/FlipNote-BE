package project.flipnote.bookmark.listener;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.bookmark.service.BookmarkService;
import project.flipnote.common.model.event.GroupLeftEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class GroupLeftCleanupBookmarkListener {

	private final BookmarkService bookmarkService;

	@Async
	@Retryable(
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleGroupLeftEvent(GroupLeftEvent event) {
		// TODO: 해당 이벤트 그룹 탈퇴시 퍼블리싱되게
		bookmarkService.removePrivateCardSetBookmarks(event.groupId(), event.userId());
	}

	@Recover
	public void recover(Exception ex, GroupLeftEvent event) {
		log.error("그룹 탈퇴 후처리 - 비공개 카드셋 북마크 제거 실패: groupId={}, userId={}", event.groupId(), event.userId(), ex);
	}
}
