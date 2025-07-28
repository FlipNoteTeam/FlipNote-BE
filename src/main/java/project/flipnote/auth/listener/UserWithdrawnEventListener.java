package project.flipnote.auth.listener;

import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.entity.AccountStatus;
import project.flipnote.auth.entity.AuthAccount;
import project.flipnote.auth.repository.AuthAccountRepository;
import project.flipnote.common.event.UserWithdrawnEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserWithdrawnEventListener {

	private final AuthAccountRepository authAccountRepository;

	@Async
	@Retryable(
		maxAttempts = 3,
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@EventListener
	public void handleUserWithdrawnEvent(UserWithdrawnEvent event) {
		authAccountRepository.findByIdAndStatus(event.userId(), AccountStatus.ACTIVE)
			.ifPresent(AuthAccount::unregister);
	}

	@Recover
	public void recover(Exception ex, UserWithdrawnEvent event) {
		log.error("회원 탈퇴 상태 변경 실패: accountId={}", event.userId(), ex);
	}
}
