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
import project.flipnote.common.model.event.GroupInvitationCreatedEvent;
import project.flipnote.notification.service.NotificationService;

@Slf4j
@RequiredArgsConstructor
@Component
public class GroupInvitationCreateEventListener {

	private final NotificationService notificationService;

	@Async
	@Retryable(
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleGroupInvitationCreatedEvent(GroupInvitationCreatedEvent event) {
		notificationService.sendGroupInvite(event.groupId(), event.inviteeId());
	}

	@Recover
	public void recover(Exception ex, GroupInvitationCreatedEvent event) {
		log.error("그룹 초대 후속 처리 예외 발생: groupId={}, inviteeId={}", event.groupId(), event.inviteeId(), ex);
	}
}
