package project.flipnote.notification.listener;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.model.event.GroupJoinRequestedEvent;
import project.flipnote.notification.service.NotificationService;

@Slf4j
@RequiredArgsConstructor
@Component
public class GroupJoinRequestedEventListener {

	private final NotificationService notificationService;

	@Async
	@Retryable(
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleGroupJoinRequestedEvent(GroupJoinRequestedEvent event) {
		notificationService.sendGroupJoinRequest(event.groupId(), event.receiverIds(), event.requesterId());
	}

	@Recover
	public void recover(Exception ex, GroupJoinRequestedEvent event) {
		log.error(
			"그룹 가입 신청 후속 처리 예외 발생: groupId={}, receiverIds={}, requesterId={}",
			event.groupId(), event.receiverIds(), event.requesterId(), ex
		);
	}
}
