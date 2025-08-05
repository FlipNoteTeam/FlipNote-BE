package project.flipnote.user.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.auth.repository.TokenVersionRedisRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.fixture.UserFixture;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.UserRegisterRequest;
import project.flipnote.user.model.UserInfoResponse;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.UserRegisterResponse;
import project.flipnote.user.model.UserUpdateRequest;
import project.flipnote.user.model.UserUpdateResponse;

@DisplayName("회원 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@Mock
	TokenVersionRedisRepository tokenVersionRedisRepository;

	@Mock
	EmailVerificationRedisRepository emailVerificationRedisRepository;

	@DisplayName("회원가입 테스트")
	@Nested
	class Register {

		@DisplayName("성공")
		@Test
		void success() {
			UserProfile userProfile = UserFixture.createActiveUser();
			UserRegisterRequest req = new UserRegisterRequest(
				"test@test.com", "testPass", "테스트", "테스트", false, "010-1234-5678", ""
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(userRepository.existsByPhone(any(String.class))).willReturn(false);
			given(emailVerificationRedisRepository.isVerified(anyString())).willReturn(true);
			given(passwordEncoder.encode(any(String.class))).willReturn("encodedPass");
			given(userRepository.save(any(UserProfile.class))).willReturn(userProfile);


			UserRegisterResponse res = userService.register(req);

			assertThat(res.userId()).isEqualTo(userProfile.getId());
		}

		@DisplayName("휴대전화 번호가 null일 때 성공")
		@Test
		void success_ifPhoneIsNull() {
			UserProfile userProfile = UserFixture.createActiveUser();
			UserRegisterRequest req = new UserRegisterRequest(
				"test@test.com", "testPass", "테스트", "테스트", false, null, null
			);

			given(userRepository.existsByEmail(any(String.class))).willReturn(false);
			given(emailVerificationRedisRepository.isVerified(anyString())).willReturn(true);
			given(passwordEncoder.encode(any(String.class))).willReturn("encodedPass");
			given(userRepository.save(any(UserProfile.class))).willReturn(userProfile);

			UserRegisterResponse res = userService.register(req);

			assertThat(res.userId()).isEqualTo(userProfile.getId());
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
			verify(userRepository, never()).save(any(UserProfile.class));
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
			given(emailVerificationRedisRepository.isVerified(anyString())).willReturn(false);

			BizException exception = assertThrows(BizException.class, () -> userService.register(req));
			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.UNVERIFIED_EMAIL);

			verify(userRepository, never()).save(any(UserProfile.class));
		}
	}

	@DisplayName("회원 탈퇴 테스트")
	@Nested
	class Unregister {

		@DisplayName("성공")
		@Test
		void success() {
			UserProfile userProfile = spy(UserFixture.createActiveUser());

			given(userRepository.findByIdAndStatus(anyLong(), any(UserStatus.class))).willReturn(Optional.of(
				userProfile));

			userService.unregister(userProfile.getId());

			assertThat(userProfile.getStatus()).isEqualTo(UserStatus.INACTIVE);
			assertThat(userProfile.getDeletedAt()).isNotNull();

			verify(userProfile, times(1)).softDelete();
			verify(tokenVersionRedisRepository, times(1)).deleteTokenVersion(anyLong());
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
			UserProfile userProfile = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", "010-9876-5432", true, "new/image.jpg"
			);
			String normalizedPhone = req.getNormalizedPhone();

			given(userRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(
				userProfile));
			given(userRepository.existsByPhone(normalizedPhone)).willReturn(false);

			UserUpdateResponse res = userService.update(userProfile.getId(), req);

			assertThat(res.userId()).isEqualTo(userProfile.getId());
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
			UserProfile userProfile = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", userProfile.getPhone(), true, "new/image.jpg"
			);
			String normalizedPhone = req.getNormalizedPhone();

			given(userRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(
				userProfile));

			UserUpdateResponse res = userService.update(userProfile.getId(), req);

			assertThat(res.userId()).isEqualTo(userProfile.getId());
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
			UserProfile userProfile = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", "010-9999-9999", true, "new/image.jpg"
			);
			String duplicatePhone = req.getNormalizedPhone();

			given(userRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(
				userProfile));
			given(userRepository.existsByPhone(duplicatePhone)).willReturn(true);

			BizException exception = assertThrows(BizException.class, () -> userService.update(userProfile.getId(), req));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_PHONE);
		}
	}

	@DisplayName("내 정보 조회 테스트")
	@Nested
	class GetMyInfo {

		@DisplayName("성공")
		@Test
		void success() {
			UserProfile userProfile = UserFixture.createActiveUser();

			given(userRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(
				userProfile));

			MyInfoResponse res = userService.getMyInfo(userProfile.getId());

			assertThat(res.userId()).isEqualTo(userProfile.getId());
			assertThat(res.email()).isEqualTo(userProfile.getEmail());
			assertThat(res.name()).isEqualTo(userProfile.getName());
			assertThat(res.nickname()).isEqualTo(userProfile.getNickname());
			assertThat(res.phone()).isEqualTo(userProfile.getPhone());
			assertThat(res.profileImageUrl()).isEqualTo(userProfile.getProfileImageUrl());
			assertThat(res.smsAgree()).isEqualTo(userProfile.isSmsAgree());

			verify(userRepository, times(1)).findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE);
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
	class GetUserProfileInfo {

		@DisplayName("성공")
		@Test
		void success() {
			UserProfile userProfile = UserFixture.createActiveUser();
			given(userRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(
				userProfile));

			UserInfoResponse res = userService.getUserInfo(userProfile.getId());

			assertThat(res.userId()).isEqualTo(userProfile.getId());
			assertThat(res.nickname()).isEqualTo(userProfile.getNickname());
			assertThat(res.profileImageUrl()).isEqualTo(userProfile.getProfileImageUrl());

			verify(userRepository, times(1)).findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE);
		}

		@DisplayName("존재하지 않는 회원 조회 시 예외 발생")
		@Test
		void fail_userNotFound() {
			given(userRepository.findByIdAndStatus(anyLong(), any(UserStatus.class))).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class, () -> userService.getUserInfo(99L));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		}
	}
}
