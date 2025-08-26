package project.flipnote.group.listener;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.config.ClientProperties;
import project.flipnote.group.model.event.GuestGroupInvitationCreateEvent;
import project.flipnote.infra.email.EmailService;

@Slf4j
@RequiredArgsConstructor
@Component
public class GuestGroupInvitationEventListener {

	private final EmailService emailService;
	private final ClientProperties clientProperties;

	@Async
	@Retryable(
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleGuestGroupInvitationCreateEvent(GuestGroupInvitationCreateEvent event) {
		emailService.sendGuestGroupInvitation(event.email(), event.groupName(), clientProperties.getUrl());
	}

	@Recover
	public void recover(Exception ex, GuestGroupInvitationCreateEvent event) {
		log.error("비회원 그룹 초대 전송 처리 예외 발생: email={}, groupName={}", event.email(), event.groupName(), ex);
	}
}
