package project.flipnote.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import project.flipnote.user.entity.User;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.UserRegisterDto;
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

	private User user;

	@BeforeEach
	void init() {
		user = User.builder()
			.email("test@test.com")
			.password("testPass")
			.name("테스트")
			.nickname("테스트")
			.smsAgree(false)
			.phone("010-1234-5678")
			.profileImageUrl(null)
			.build();

		ReflectionTestUtils.setField(user, "id", 1L);
	}

	@DisplayName("회원가입 테스트")
	@Nested
	class Register {

		@DisplayName("성공")
		@Test
		void success() {
			UserRegisterDto.Request req = new UserRegisterDto.Request(
				"test@test.com", "testPass", "테스트", "테스트", false, "010-1234-5678", ""
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(userRepository.existsByPhone(any(String.class))).willReturn(false);
			given(passwordEncoder.encode(any(String.class))).willReturn("encodedPass");
			given(userRepository.save(any(User.class))).willReturn(user);

			UserRegisterDto.Response res = userService.register(req);

			assertThat(res.userId()).isEqualTo(user.getId());

			verify(authService, times(1)).validateEmail(any(String.class));
			verify(authService, times(1)).deleteVerifiedEmail(any(String.class));
		}

		@DisplayName("휴대전화 번호가 null일 때 성공")
		@Test
		void success_ifPhoneIsNull() {
			UserRegisterDto.Request req = new UserRegisterDto.Request(
				"test@test.com", "testPass", "테스트", "테스트", false, null, null
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(passwordEncoder.encode(any(String.class))).willReturn("encodedPass");
			given(userRepository.save(any(User.class))).willReturn(user);

			UserRegisterDto.Response res = userService.register(req);

			assertThat(res.userId()).isEqualTo(user.getId());

			verify(authService, times(1)).validateEmail(any(String.class));
			verify(authService, times(1)).deleteVerifiedEmail(any(String.class));
		}

		@DisplayName("이메일 중복 시 예외 발생")
		@Test
		void fail_duplicateEmail() {
			UserRegisterDto.Request req = new UserRegisterDto.Request(
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
			UserRegisterDto.Request req = new UserRegisterDto.Request(
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
			UserRegisterDto.Request req = new UserRegisterDto.Request(
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

}
