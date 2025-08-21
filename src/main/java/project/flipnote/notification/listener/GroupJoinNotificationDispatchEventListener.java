package project.flipnote.notification.listener;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.notification.model.GroupJoinNotificationDispatchEvent;
import project.flipnote.notification.service.NotificationService;

@Slf4j
@RequiredArgsConstructor
@Component
public class GroupJoinNotificationDispatchEventListener {

	private final NotificationService notificationService;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleGroupJoinNotificationDispatchEvent(GroupJoinNotificationDispatchEvent event) {
		notificationService.sendGroupJoinRequestNotifications(event.notifications());
	}
}
