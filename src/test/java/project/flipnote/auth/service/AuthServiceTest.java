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
import org.springframework.security.crypto.password.PasswordEncoder;

import project.flipnote.auth.event.EmailVerificationSendEvent;
import project.flipnote.auth.event.PasswordResetCreateEvent;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.model.EmailVerificationConfirmRequest;
import project.flipnote.auth.model.EmailVerificationRequest;
import project.flipnote.auth.model.PasswordResetCreateRequest;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.model.UserLoginRequest;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.auth.repository.PasswordResetRedisRepository;
import project.flipnote.auth.repository.TokenBlacklistRedisRepository;
import project.flipnote.auth.util.PasswordResetTokenGenerator;
import project.flipnote.auth.util.VerificationCodeGenerator;
import project.flipnote.common.config.ClientProperties;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.common.security.exception.CustomSecurityException;
import project.flipnote.common.security.exception.SecurityErrorCode;
import project.flipnote.common.security.jwt.JwtComponent;
import project.flipnote.fixture.UserFixture;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
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

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	JwtComponent jwtComponent;

	@Mock
	TokenBlacklistRedisRepository tokenBlacklistRedisRepository;

	@Mock
	VerificationCodeGenerator verificationCodeGenerator;

	@Mock
	PasswordResetRedisRepository passwordResetRedisRepository;

	@Mock
	PasswordResetTokenGenerator passwordResetTokenGenerator;

	@Mock
	ClientProperties clientProperties;

	@DisplayName("이메일 인증번호 전송 테스트")
	@Nested
	class SendEmailVerificationCode {

		@DisplayName("성공")
		@Test
		void success() {
			String email = "test@test.com";
			String code = "123456";
			EmailVerificationRequest req = new EmailVerificationRequest(email);

			given(userRepository.existsByEmail(anyString())).willReturn(false);
			given(emailVerificationRedisRepository.existCode(anyString())).willReturn(false);
			given(verificationCodeGenerator.generateVerificationCode(anyInt())).willReturn(code);

			authService.sendEmailVerificationCode(req);

			verify(emailVerificationRedisRepository).saveCode(eq("test@test.com"), eq(code));
			verify(eventPublisher, times(1)).publishEvent(any(EmailVerificationSendEvent.class));
		}

		@DisplayName("가입된 이메일인 경우 예외 발생")
		@Test
		void fail_existingEmail() {
			EmailVerificationRequest req = new EmailVerificationRequest("test@test.com");

			given(userRepository.existsByEmail(any(String.class))).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> authService.sendEmailVerificationCode(req));
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.EXISTING_EMAIL);

			verify(emailVerificationRedisRepository, never()).saveCode(any(String.class), any(String.class));
			verify(eventPublisher, never()).publishEvent(any(EmailVerificationSendEvent.class));
		}

		@DisplayName("이미 발급된 인증번호가 존재할 경우 예외 발생")
		@Test
		void fail_alreadyIssuedVerificationCode() {
			EmailVerificationRequest req = new EmailVerificationRequest("test@test.com");

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
			EmailVerificationConfirmRequest req = new EmailVerificationConfirmRequest("test@test.com", "123456");

			given(emailVerificationRedisRepository.findCode("test@test.com"))
				.willReturn(Optional.of("123456"));

			authService.confirmEmailVerificationCode(req);

			verify(emailVerificationRedisRepository, times(1)).deleteCode(any(String.class));
			verify(emailVerificationRedisRepository, times(1)).markAsVerified(any(String.class));
		}

		@DisplayName("발급된 인증번호가 없는 경우 예외 발생")
		@Test
		void fail_notIssuedVerificationCode() {
			EmailVerificationConfirmRequest req = new EmailVerificationConfirmRequest("test@test.com", "123456");

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
			EmailVerificationConfirmRequest req = new EmailVerificationConfirmRequest("test@test.com", "123456");

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

	@DisplayName("로그인 테스트")
	@Nested
	class Login {

		@DisplayName("성공")
		@Test
		void success() {
			UserLoginRequest req = new UserLoginRequest("test@example.com", "testPass");

			User foundUser = UserFixture.createActiveUser();

			TokenPair expectedTokenPair = new TokenPair("access-token", "refresh-token");

			given(userRepository.findByEmailAndStatus(req.email(), UserStatus.ACTIVE))
				.willReturn(Optional.of(foundUser));
			given(passwordEncoder.matches(req.password(), foundUser.getPassword()))
				.willReturn(true);
			given(jwtComponent.generateTokenPair(foundUser)).willReturn(expectedTokenPair);

			TokenPair resultTokenPair = authService.login(req);

			assertThat(resultTokenPair).isNotNull();
			assertThat(resultTokenPair.accessToken()).isEqualTo(expectedTokenPair.accessToken());
			assertThat(resultTokenPair.refreshToken()).isEqualTo(expectedTokenPair.refreshToken());

			verify(userRepository).findByEmailAndStatus(anyString(), any(UserStatus.class));
			verify(passwordEncoder).matches(anyString(), anyString());
			verify(jwtComponent).generateTokenPair(any(User.class));
		}

		@Test
		@DisplayName("이메일이 존재하지 않는 경우 예외 발생")
		void fail_invalidCredentials_wrongEmail() {
			UserLoginRequest req = new UserLoginRequest("wrong@test.com", "testPass");

			when(userRepository.findByEmailAndStatus(req.email(), UserStatus.ACTIVE))
				.thenReturn(Optional.empty());

			BizException exception = assertThrows(
				BizException.class,
				() -> authService.login(req)
			);

			assertThat(exception).isNotNull();
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);

			verify(passwordEncoder, never()).matches(anyString(), anyString());
			verify(jwtComponent, never()).generateTokenPair(any(User.class));
		}

		@Test
		@DisplayName("비밀번호가 일치하지 않는 경우 예외 발생")
		void fail_invalidCredentials_wrongPassword() {
			UserLoginRequest req = new UserLoginRequest("wrong@test.com", "wrongPass");

			User foundUser = UserFixture.createActiveUser();

			given(userRepository.findByEmailAndStatus(req.email(), UserStatus.ACTIVE))
				.willReturn(Optional.of(foundUser));
			given(passwordEncoder.matches(req.password(), foundUser.getPassword()))
				.willReturn(false);

			BizException exception = assertThrows(
				BizException.class,
				() -> authService.login(req)
			);

			assertThat(exception).isNotNull();
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);

			verify(jwtComponent, never()).generateTokenPair(any(User.class));
		}
	}

	@DisplayName("토큰 갱신 테스트")
	@Nested
	class RefreshToken {

		@DisplayName("성공")
		@Test
		void success() {
			String refreshToken = "valid-refresh-token";
			long expirationMillis = System.currentTimeMillis() + 100000;
			UserAuth userAuth = UserAuth.from(UserFixture.createActiveUser());
			TokenPair expectedTokenPair = new TokenPair("new-access-token", "new-refresh-token");

			given(tokenBlacklistRedisRepository.exist(refreshToken)).willReturn(false);
			given(jwtComponent.getExpirationMillis(refreshToken)).willReturn(expirationMillis);
			given(jwtComponent.extractUserAuthFromToken(refreshToken)).willReturn(userAuth);
			given(jwtComponent.generateTokenPair(userAuth)).willReturn(expectedTokenPair);

			TokenPair resultTokenPair = authService.refreshToken(refreshToken);

			assertThat(resultTokenPair).isNotNull();
			assertThat(resultTokenPair.accessToken()).isEqualTo(expectedTokenPair.accessToken());
			assertThat(resultTokenPair.refreshToken()).isEqualTo(expectedTokenPair.refreshToken());

			verify(tokenBlacklistRedisRepository, times(1)).exist(refreshToken);
			verify(jwtComponent, times(1)).getExpirationMillis(refreshToken);
			verify(tokenBlacklistRedisRepository, times(1)).save(refreshToken, expirationMillis);
			verify(jwtComponent, times(1)).extractUserAuthFromToken(refreshToken);
			verify(jwtComponent, times(1)).generateTokenPair(userAuth);
		}

		@DisplayName("이미 사용된 토큰(블랙리스트)인 경우 예외 발생")
		@Test
		void fail_whenTokenIsBlacklisted() {
			String refreshToken = "blacklisted-refresh-token";
			given(tokenBlacklistRedisRepository.exist(refreshToken)).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> authService.refreshToken(refreshToken));
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_REFRESH_TOKEN);

			verify(jwtComponent, never()).getExpirationMillis(anyString());
			verify(tokenBlacklistRedisRepository, never()).save(anyString(), anyLong());
			verify(jwtComponent, never()).extractUserAuthFromToken(anyString());
		}

		@DisplayName("유효하지 않은 토큰으로 갱신 시도 시 예외 발생")
		@Test
		void fail_whenTokenIsInvalid() {
			String invalidToken = "invalid-refresh-token";
			long expirationMillis = 1000L;
			given(tokenBlacklistRedisRepository.exist(invalidToken)).willReturn(false);
			given(jwtComponent.getExpirationMillis(invalidToken)).willReturn(expirationMillis);
			given(jwtComponent.extractUserAuthFromToken(invalidToken))
				.willThrow(new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN));

			CustomSecurityException exception = assertThrows(
				CustomSecurityException.class,
				() -> authService.refreshToken(invalidToken)
			);
			assertThat(exception.getErrorCode()).isEqualTo(SecurityErrorCode.NOT_VALID_JWT_TOKEN);

			verify(tokenBlacklistRedisRepository, times(1)).save(invalidToken, expirationMillis);
			verify(jwtComponent, never()).generateTokenPair(any(UserAuth.class));
		}
	}

	@DisplayName("비밀번호 재설정 링크 전송 테스트")
	@Nested
	class RequestPasswordReset {

		@DisplayName("성공")
		@Test
		void success() {
			String email = "test@test.com";
			String token = "test-token";
			PasswordResetCreateRequest req = new PasswordResetCreateRequest(email);

			given(passwordResetRedisRepository.hasActiveToken(anyString())).willReturn(false);
			given(userRepository.existsByEmailAndStatus(anyString(), any())).willReturn(true);
			given(passwordResetTokenGenerator.generateToken()).willReturn(token);

			authService.requestPasswordReset(req);

			verify(passwordResetRedisRepository, times(1)).saveEmail(eq(email));
			verify(passwordResetRedisRepository, times(1)).saveToken(eq(email), eq(token));
			verify(eventPublisher, times(1)).publishEvent(any(PasswordResetCreateEvent.class));
		}

		@DisplayName("비밀번호 재설정 링크가 존재하는 경우 예외 발생")
		@Test
		void fail_alreadySentPasswordResetLink() {
			PasswordResetCreateRequest req = new PasswordResetCreateRequest("test@test.com");

			given(passwordResetRedisRepository.hasActiveToken(anyString())).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> authService.requestPasswordReset(req));
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.ALREADY_SENT_PASSWORD_RESET_LINK);

			verify(passwordResetRedisRepository, never()).saveEmail(anyString());
			verify(passwordResetRedisRepository, never()).saveToken(anyString(), anyString());
			verify(eventPublisher, never()).publishEvent(any(PasswordResetCreateEvent.class));
		}

		@DisplayName("존재하지 않는 이메일의 경우 동작 안함 (예외는 발생 안함)")
		@Test
		void fail_notExistEmail() {
			PasswordResetCreateRequest req = new PasswordResetCreateRequest("test@test.com");

			given(passwordResetRedisRepository.hasActiveToken(anyString())).willReturn(false);
			given(userRepository.existsByEmailAndStatus(anyString(), any())).willReturn(false);

			authService.requestPasswordReset(req);

			verify(passwordResetRedisRepository, never()).saveEmail(anyString());
			verify(passwordResetRedisRepository, never()).saveToken(anyString(), anyString());
			verify(eventPublisher, never()).publishEvent(any(PasswordResetCreateEvent.class));
		}
	}
}
