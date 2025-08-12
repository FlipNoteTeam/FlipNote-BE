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
import project.flipnote.common.event.UserRegisteredEvent;
import project.flipnote.group.service.GroupInvitationService;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserRegisteredEventListener {

	private final GroupInvitationService groupInvitationService;

	@Async
	@Retryable(
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void HandleUserRegisteredEvent(UserRegisteredEvent event) {
		groupInvitationService.acceptPendingInvitationsOnRegister(event.userId(), event.email());
	}

	@Recover
	public void recover(Exception ex, UserRegisteredEvent event) {
		log.error("회원가입 후속 처리 예외 발생: userId={}, email={}", event.userId(), event.email(), ex);
	}
}
