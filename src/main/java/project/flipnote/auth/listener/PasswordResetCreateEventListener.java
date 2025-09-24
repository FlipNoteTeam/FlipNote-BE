package project.flipnote.auth.listener;

import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.constants.PasswordResetConstants;
import project.flipnote.auth.model.event.PasswordResetCreateEvent;
import project.flipnote.common.exception.EmailSendException;
import project.flipnote.infra.email.EmailService;

@Slf4j
@RequiredArgsConstructor
@Component
public class PasswordResetCreateEventListener {

	private final EmailService emailService;

	@Async
	@Retryable(
		maxAttempts = 3,
		retryFor = {EmailSendException.class},
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@EventListener()
	public void handlePasswordResetCreateEvent(PasswordResetCreateEvent event) {
		emailService.sendPasswordResetLink(event.to(), event.link(), PasswordResetConstants.TOKEN_TTL_MINUTES);
	}

	@Recover
	public void recover(EmailSendException ex, PasswordResetCreateEvent event) {
		log.error("비밀번호 재설정 링크 전송 실패: to={}", event.to(), ex);
	}
}
