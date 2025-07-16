package project.flipnote.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.service.AuthService;
import project.flipnote.common.exception.BizException;
import project.flipnote.fixture.UserFixture;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.UserRegisterRequest;
import project.flipnote.user.model.UserRegisterResponse;
import project.flipnote.user.repository.UserRepository;

@DisplayName("회원 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private AuthService authService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@DisplayName("회원가입 테스트")
	@Nested
	class Register {

		@DisplayName("성공")
		@Test
		void success() {
			User user = UserFixture.createActiveUser();
			UserRegisterRequest req = new UserRegisterRequest(
				"test@test.com", "testPass", "테스트", "테스트", false, "010-1234-5678", ""
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(userRepository.existsByPhone(any(String.class))).willReturn(false);
			given(passwordEncoder.encode(any(String.class))).willReturn("encodedPass");
			given(userRepository.save(any(User.class))).willReturn(user);

			UserRegisterResponse res = userService.register(req);

			assertThat(res.userId()).isEqualTo(user.getId());

			verify(authService, times(1)).validateEmail(any(String.class));
			verify(authService, times(1)).deleteVerifiedEmail(any(String.class));
		}

		@DisplayName("휴대전화 번호가 null일 때 성공")
		@Test
		void success_ifPhoneIsNull() {
			User user = UserFixture.createActiveUser();
			UserRegisterRequest req = new UserRegisterRequest(
				"test@test.com", "testPass", "테스트", "테스트", false, null, null
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(passwordEncoder.encode(any(String.class))).willReturn("encodedPass");
			given(userRepository.save(any(User.class))).willReturn(user);

			UserRegisterResponse res = userService.register(req);

			assertThat(res.userId()).isEqualTo(user.getId());

			verify(authService, times(1)).validateEmail(any(String.class));
			verify(authService, times(1)).deleteVerifiedEmail(any(String.class));
		}

		@DisplayName("이메일 중복 시 예외 발생")
		@Test
		void fail_duplicateEmail() {
			UserRegisterRequest req = new UserRegisterRequest(
				"test@test.com", "testPass", "테스트", "테스트", false, "010-1234-5678", ""
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> userService.register(req));
			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_EMAIL);

			verify(userRepository, never()).existsByPhone(any(String.class));
			verify(userRepository, never()).save(any(User.class));
		}

		@DisplayName("전화번호 중복 시 예외 발생")
		@Test
		void fail_duplicatePhone() {
			UserRegisterRequest req = new UserRegisterRequest(
				"test@test.com", "testPass", "테스트", "테스트", false, "010-1234-5678", ""
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(userRepository.existsByPhone(any(String.class))).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> userService.register(req));
			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_PHONE);

			verify(userRepository, never()).save(any());
		}

		@DisplayName("이메일 인증이 안 된 경우 예외 발생")
		@Test
		void fail_unverifiedEmail() {
			UserRegisterRequest req = new UserRegisterRequest(
				"test@test.com", "testPass", "테스트", "테스트", false, "010-1234-5678", ""
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(userRepository.existsByPhone(any(String.class))).willReturn(false);
			doThrow(new BizException(AuthErrorCode.UNVERIFIED_EMAIL))
				.when(authService).validateEmail(any(String.class));

			BizException exception = assertThrows(BizException.class, () -> userService.register(req));
			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.UNVERIFIED_EMAIL);

			verify(userRepository, never()).save(any(User.class));
		}
	}

	@DisplayName("회원 탈퇴 테스트")
	@Nested
	class Unregister {

		@DisplayName("성공")
		@Test
		void success() {
			User user = spy(UserFixture.createActiveUser());

			given(userRepository.findByIdAndStatus(anyLong(), any(UserStatus.class))).willReturn(Optional.of(user));

			userService.unregister(user.getId());

			assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
			assertThat(user.getDeletedAt()).isNotNull();

			verify(user, times(1)).softDelete();
		}

		@DisplayName("회원 id가 존재하지 않는 경우 예외 발생")
		@Test
		void fail_userNotFound() {
			given(userRepository.findByIdAndStatus(anyLong(), any(UserStatus.class))).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class, () -> userService.unregister(1L));
			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		}
	}
}
