package project.flipnote.auth.listener;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.constants.VerificationConstants;
import project.flipnote.auth.model.event.EmailVerificationSendEvent;
import project.flipnote.common.exception.EmailSendException;
import project.flipnote.infra.email.EmailService;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmailVerificationEventListener {

	private final EmailService emailService;

	@Async
	@Retryable(
		maxAttempts = 3,
		retryFor = { EmailSendException.class },
		backoff = @Backoff(delay = 2000, multiplier = 2)
	)
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleEmailVerificationSendEvent(EmailVerificationSendEvent event) {
		emailService.sendEmailVerificationCode(event.to(), event.code(), VerificationConstants.CODE_TTL_MINUTES);
	}

	@Recover
	public void recover(EmailSendException ex, EmailVerificationSendEvent event) {
		log.error("이메일 인증번호 전송 실패: to={}", event.to(), ex);
	}
}
