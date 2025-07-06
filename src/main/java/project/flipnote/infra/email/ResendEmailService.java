package project.flipnote.infra.email;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.EmailSendException;
import project.flipnote.infra.config.ResendProperties;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResendEmailService implements EmailService {

	private final ResendProperties resendProperties;
	private final Resend resend;
	private final SpringTemplateEngine templateEngine;

	@Override
	public void sendEmailVerificationCode(String to, String code, int ttl) {
		Context context = new Context();
		context.setVariable("code", code);
		context.setVariable("validMinutes", ttl);

		String html = templateEngine.process("email/email-verification", context);

		CreateEmailOptions params = CreateEmailOptions.builder()
			.from(resendProperties.getFromEmail())
			.to(to)
			.subject("이메일 인증번호 안내")
			.html(html)
			.build();

		try {
			resend.emails().send(params);
		} catch (ResendException e) {
			log.error("이메일 인증번호 발송 실패: to={}, code={}, ttl={}분", to, code, ttl, e);
			throw new EmailSendException(e);
		}
	}
}
