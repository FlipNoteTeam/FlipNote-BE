package project.flipnote.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import project.flipnote.auth.constants.VerificationConstants;
import project.flipnote.auth.event.EmailVerificationSendEvent;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.model.EmailVerificationConfirmDto;
import project.flipnote.auth.model.EmailVerificationDto;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.user.repository.UserRepository;

@DisplayName("인증 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@InjectMocks
	AuthService authService;

	@Mock
	EmailVerificationRedisRepository emailVerificationRedisRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	ApplicationEventPublisher eventPublisher;

	@DisplayName("이메일 인증번호 전송 테스트")
	@Nested
	class SendEmailVerificationCode {

		@DisplayName("성공")
		@Test
		void success() {
			EmailVerificationDto.Request req = new EmailVerificationDto.Request("test@test.com");

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(emailVerificationRedisRepository.existCode(any(String.class))).willReturn(false);

			authService.sendEmailVerificationCode(req);

			verify(emailVerificationRedisRepository, times(1)).saveCode(any(String.class), any(String.class));
			verify(eventPublisher, times(1)).publishEvent(any(EmailVerificationSendEvent.class));

			int codeLength = VerificationConstants.CODE_LENGTH;
			verify(emailVerificationRedisRepository).saveCode(
				eq(req.email()),
				argThat(code -> code.length() == codeLength && code.matches("\\d{%s}".formatted(codeLength)))
			);
		}

		@DisplayName("가입된 이메일인 경우 예외 발생")
		@Test
		void fail_existingEmail() {
			EmailVerificationDto.Request req = new EmailVerificationDto.Request("test@test.com");

			given(userRepository.existsByEmail(any(String.class))).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> authService.sendEmailVerificationCode(req));
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EXISTING_EMAIL);

			verify(emailVerificationRedisRepository, never()).saveCode(any(String.class), any(String.class));
			verify(eventPublisher, never()).publishEvent(any(EmailVerificationSendEvent.class));
		}

		@DisplayName("이미 발급된 인증번호가 존재할 경우 예외 발생")
		@Test
		void fail_alreadyIssuedVerificationCode() {
			EmailVerificationDto.Request req = new EmailVerificationDto.Request("test@test.com");

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(emailVerificationRedisRepository.existCode(any(String.class))).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> authService.sendEmailVerificationCode(req));
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.ALREADY_ISSUED_VERIFICATION_CODE);

			verify(emailVerificationRedisRepository, never()).saveCode(any(String.class), any(String.class));
			verify(eventPublisher, never()).publishEvent(any(EmailVerificationSendEvent.class));
		}
	}

	@DisplayName("이메일 인증번호 확인 테스트")
	@Nested
	class ConfirmEmailVerificationCode {

		@DisplayName("성공")
		@Test
		void success() {
			EmailVerificationConfirmDto.Request req
				= new EmailVerificationConfirmDto.Request("test@test.com", "123456");

			given(emailVerificationRedisRepository.findCode("test@test.com"))
				.willReturn(Optional.of("123456"));

			authService.confirmEmailVerificationCode(req);

			verify(emailVerificationRedisRepository, times(1)).deleteCode(any(String.class));
			verify(emailVerificationRedisRepository, times(1)).markAsVerified(any(String.class));
		}

		@DisplayName("발급된 인증번호가 없는 경우 예외 발생")
		@Test
		void fail_notIssuedVerificationCode() {
			EmailVerificationConfirmDto.Request req
				= new EmailVerificationConfirmDto.Request("test@test.com", "123456");

			given(emailVerificationRedisRepository.findCode("test@test.com")).willReturn(Optional.empty());

			BizException exception = assertThrows(
				BizException.class,
				() -> authService.confirmEmailVerificationCode(req)
			);
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.NOT_ISSUED_VERIFICATION_CODE);

			verify(emailVerificationRedisRepository, never()).deleteCode(any(String.class));
			verify(emailVerificationRedisRepository, never()).markAsVerified(any(String.class));
		}

		@DisplayName("잘못된 인증번호인 경우 예외 발생")
		@Test
		void fail_invalidVerificationCode() {
			EmailVerificationConfirmDto.Request req
				= new EmailVerificationConfirmDto.Request("test@test.com", "123456");

			given(emailVerificationRedisRepository.findCode("test@test.com"))
				.willReturn(Optional.of("654321"));

			BizException exception = assertThrows(
				BizException.class,
				() -> authService.confirmEmailVerificationCode(req)
			);
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_VERIFICATION_CODE);

			verify(emailVerificationRedisRepository, never()).deleteCode(any(String.class));
			verify(emailVerificationRedisRepository, never()).markAsVerified(any(String.class));
		}

	}
}
