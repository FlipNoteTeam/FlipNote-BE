package project.flipnote.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

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
import project.flipnote.auth.repository.TokenVersionRedisRepository;
import project.flipnote.auth.service.AuthService;
import project.flipnote.auth.service.EmailVerificationService;
import project.flipnote.auth.service.TokenVersionService;
import project.flipnote.common.exception.BizException;
import project.flipnote.fixture.UserFixture;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserOAuthLink;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.ChangePasswordRequest;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.SocialLinksResponse;
import project.flipnote.user.model.UserInfoResponse;
import project.flipnote.user.model.UserRegisterRequest;
import project.flipnote.user.model.UserRegisterResponse;
import project.flipnote.user.model.UserUpdateRequest;
import project.flipnote.user.model.UserUpdateResponse;
import project.flipnote.user.repository.UserOAuthLinkRepository;
import project.flipnote.user.repository.UserRepository;

@DisplayName("회원 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	TokenVersionRedisRepository tokenVersionRedisRepository;

	@Mock
	AuthService authService;

	@Mock
	TokenVersionService tokenVersionService;

	@Mock
	EmailVerificationService emailVerificationService;

	@Mock
	UserOAuthLinkRepository userOAuthLinkRepository;

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

			given(userRepository.existsByEmail(anyString())).willReturn(false);
			given(userRepository.existsByPhone(anyString())).willReturn(false);
			doThrow(new BizException(AuthErrorCode.UNVERIFIED_EMAIL))
				.when(emailVerificationService)
				.validateVerified(anyString());

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

			verify(user, times(1)).unregister();
			verify(tokenVersionService, times(1)).incrementTokenVersion(user.getId());
		}

		@DisplayName("회원 id가 존재하지 않는 경우 예외 발생")
		@Test
		void fail_userNotFound() {
			given(userRepository.findByIdAndStatus(anyLong(), any(UserStatus.class))).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class, () -> userService.unregister(1L));
			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		}
	}

	@DisplayName("회원 정보 수정 테스트")
	@Nested
	class Update {

		@DisplayName("성공")
		@Test
		void success() {
			User user = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", "010-9876-5432", true, "new/image.jpg"
			);
			String normalizedPhone = req.getNormalizedPhone();

			given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
			given(userRepository.existsByPhone(normalizedPhone)).willReturn(false);

			UserUpdateResponse res = userService.update(user.getId(), req);

			assertThat(res.userId()).isEqualTo(user.getId());
			assertThat(res.nickname()).isEqualTo(req.nickname());
			assertThat(res.phone()).isEqualTo(normalizedPhone);
			assertThat(res.smsAgree()).isEqualTo(req.smsAgree());
			assertThat(res.profileImageUrl()).isEqualTo(req.profileImageUrl());

			verify(userRepository, times(1)).findByIdAndStatus(anyLong(), any(UserStatus.class));
			verify(userRepository, times(1)).existsByPhone(anyString());
		}

		@DisplayName("동일한 전화번호로 수정 시 성공")
		@Test
		void success_withSamePhone() {
			User user = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", user.getPhone(), true, "new/image.jpg"
			);
			String normalizedPhone = req.getNormalizedPhone();

			given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));

			UserUpdateResponse res = userService.update(user.getId(), req);

			assertThat(res.userId()).isEqualTo(user.getId());
			assertThat(res.nickname()).isEqualTo(req.nickname());
			assertThat(res.phone()).isEqualTo(normalizedPhone);
			assertThat(res.smsAgree()).isEqualTo(req.smsAgree());
			assertThat(res.profileImageUrl()).isEqualTo(req.profileImageUrl());

			verify(userRepository, never()).existsByPhone(anyString());
		}

		@DisplayName("존재하지 않는 회원 수정 시 예외 발생")
		@Test
		void fail_userNotFound() {
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", "010-9876-5432", true, "new/image.jpg"
			);

			given(userRepository.findByIdAndStatus(anyLong(), any(UserStatus.class))).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class, () -> userService.update(99L, req));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		}

		@DisplayName("중복된 전화번호로 수정 시 예외 발생")
		@Test
		void fail_duplicatePhone() {
			User user = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", "010-9999-9999", true, "new/image.jpg"
			);
			String duplicatePhone = req.getNormalizedPhone();

			given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
			given(userRepository.existsByPhone(duplicatePhone)).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> userService.update(user.getId(), req));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_PHONE);
		}
	}

	@DisplayName("내 정보 조회 테스트")
	@Nested
	class GetMyInfo {

		@DisplayName("성공")
		@Test
		void success() {
			User user = UserFixture.createActiveUser();

			given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));

			MyInfoResponse res = userService.getMyInfo(user.getId());

			assertThat(res.userId()).isEqualTo(user.getId());
			assertThat(res.email()).isEqualTo(user.getEmail());
			assertThat(res.name()).isEqualTo(user.getName());
			assertThat(res.nickname()).isEqualTo(user.getNickname());
			assertThat(res.phone()).isEqualTo(user.getPhone());
			assertThat(res.profileImageUrl()).isEqualTo(user.getProfileImageUrl());
			assertThat(res.smsAgree()).isEqualTo(user.isSmsAgree());

			verify(userRepository, times(1)).findByIdAndStatus(user.getId(), UserStatus.ACTIVE);
		}

		@DisplayName("존재하지 않는 회원 조회 시 예외 발생")
		@Test
		void fail_userNotFound() {
			given(userRepository.findByIdAndStatus(anyLong(), any(UserStatus.class))).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class, () -> userService.getMyInfo(99L));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		}
	}

	@DisplayName("다른 회원 정보 조회 테스트")
	@Nested
	class GetUserInfo {

		@DisplayName("성공")
		@Test
		void success() {
			User user = UserFixture.createActiveUser();
			given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));

			UserInfoResponse res = userService.getUserInfo(user.getId());

			assertThat(res.userId()).isEqualTo(user.getId());
			assertThat(res.nickname()).isEqualTo(user.getNickname());
			assertThat(res.profileImageUrl()).isEqualTo(user.getProfileImageUrl());

			verify(userRepository, times(1)).findByIdAndStatus(user.getId(), UserStatus.ACTIVE);
		}

		@DisplayName("존재하지 않는 회원 조회 시 예외 발생")
		@Test
		void fail_userNotFound() {
			given(userRepository.findByIdAndStatus(anyLong(), any(UserStatus.class))).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class, () -> userService.getUserInfo(99L));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		}
	}

	@DisplayName("비밀번호 변경 테스트")
	@Nested
	class ChangePassword {

		@DisplayName("성공")
		@Test
		void success() {
			User user = spy(UserFixture.createActiveUser());
			ChangePasswordRequest req = new ChangePasswordRequest("currentPassword123!", "newPassword123!");
			String encodedNewPassword = "encodedNewPassword";

			given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
			given(passwordEncoder.encode(req.newPassword())).willReturn(encodedNewPassword);

			userService.changePassword(user.getId(), req);

			verify(user, times(1)).changePassword(encodedNewPassword);
			verify(tokenVersionService, times(1)).incrementTokenVersion(user.getId());
		}

		@DisplayName("존재하지 않는 회원의 비밀번호 변경 시 예외 발생")
		@Test
		void fail_userNotFound() {
			ChangePasswordRequest req = new ChangePasswordRequest("currentPassword123!", "newPassword123!");
			Long nonExistentUserId = 99L;

			given(userRepository.findByIdAndStatus(nonExistentUserId, UserStatus.ACTIVE)).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class,
				() -> userService.changePassword(nonExistentUserId, req));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);

			verify(passwordEncoder, never()).matches(anyString(), anyString());
			verify(passwordEncoder, never()).encode(anyString());
			verify(tokenVersionRedisRepository, never()).deleteTokenVersion(anyLong());
		}

		@DisplayName("현재 비밀번호가 일치하지 않을 경우 예외 발생")
		@Test
		void fail_incorrectCurrentPassword() {
			User user = UserFixture.createActiveUser();
			ChangePasswordRequest req = new ChangePasswordRequest("wrongPassword", "newPassword123!");

			given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
			doThrow(new BizException(AuthErrorCode.INVALID_CREDENTIALS))
				.when(authService)
				.validatePasswordMatch(req.currentPassword(), user.getPassword());

			BizException exception = assertThrows(BizException.class,
				() -> userService.changePassword(user.getId(), req));

			assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.INVALID_CREDENTIALS);
			verify(authService, times(1)).validatePasswordMatch(req.currentPassword(), user.getPassword());
			verify(passwordEncoder, never()).encode(anyString());
			verify(tokenVersionRedisRepository, never()).deleteTokenVersion(anyLong());
		}
	}

	@DisplayName("내 소셜 계정 목록 조회 테스트")
	@Nested
	class GetSocialLinks {

		@DisplayName("성공")
		@Test
		void success() {
			User user = UserFixture.createActiveUser();

			List<UserOAuthLink> links = List.of(new UserOAuthLink("google", "providerId1", user));

			given(userOAuthLinkRepository.findByUser_Id(user.getId())).willReturn(links);

			SocialLinksResponse res = userService.getSocialLinks(user.getId());

			assertThat(res.socialLinks()).isNotNull();
			assertThat(res.socialLinks().size()).isEqualTo(1);
			assertThat(res.socialLinks().get(0).provider()).isEqualTo("google");
		}
	}

	@DisplayName("소셜 연동 해제 테스트")
	@Nested
	class DeleteSocialLink {

		@DisplayName("성공")
		@Test
		void success() {
			User user = UserFixture.createActiveUser();
			UserOAuthLink userOAuthLink = new UserOAuthLink("google", "providerId", user);
			ReflectionTestUtils.setField(userOAuthLink, "id", 1L);

			given(userOAuthLinkRepository.existsByIdAndUser_Id(userOAuthLink.getId(), user.getId())).willReturn(true);

			userService.deleteSocialLink(user.getId(), userOAuthLink.getId());

			verify(userOAuthLinkRepository, times(1)).deleteById(userOAuthLink.getId());
		}

		@DisplayName("회원이 연동한 소셜 계정이 아닌 경우 예외 발생")
		@Test
		void fail_socialLinkNotFound() {
			Long userId = 1L;
			Long socialLinkId = 1L;

			given(userOAuthLinkRepository.existsByIdAndUser_Id(socialLinkId, userId)).willReturn(false);

			BizException exception = assertThrows(BizException.class,
				() -> userService.deleteSocialLink(userId, socialLinkId));
			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.SOCIAL_LINK_NOT_FOUND);

			verify(userOAuthLinkRepository, never()).deleteById(anyLong());
		}
	}
}