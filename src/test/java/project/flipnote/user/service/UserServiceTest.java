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

import project.flipnote.common.exception.BizException;
import project.flipnote.fixture.UserFixture;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.auth.entity.AccountStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.UserInfoResponse;
import project.flipnote.user.model.UserUpdateRequest;
import project.flipnote.user.model.UserUpdateResponse;
import project.flipnote.user.repository.UserRepository;

@DisplayName("회원 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks
	UserService userService;

	@Mock
	UserRepository userRepository;

	@DisplayName("회원 정보 수정 테스트")
	@Nested
	class Update {

		@DisplayName("성공")
		@Test
		void success() {
			UserProfile user = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", "010-9876-5432", true, "new/image.jpg"
			);
			String normalizedPhone = req.getNormalizedPhone();

			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
			given(userRepository.existsByPhone(normalizedPhone)).willReturn(false);

			UserUpdateResponse res = userService.update(user.getId(), req);

			assertThat(res.userId()).isEqualTo(user.getId());
			assertThat(res.nickname()).isEqualTo(req.nickname());
			assertThat(res.phone()).isEqualTo(normalizedPhone);
			assertThat(res.smsAgree()).isEqualTo(req.smsAgree());
			assertThat(res.profileImageUrl()).isEqualTo(req.profileImageUrl());

			verify(userRepository, times(1)).findById(anyLong());
			verify(userRepository, times(1)).existsByPhone(anyString());
		}

		@DisplayName("동일한 전화번호로 수정 시 성공")
		@Test
		void success_withSamePhone() {
			UserProfile user = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", user.getPhone(), true, "new/image.jpg"
			);
			String normalizedPhone = req.getNormalizedPhone();

			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

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

			given(userRepository.findById(anyLong())).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class, () -> userService.update(99L, req));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		}

		@DisplayName("중복된 전화번호로 수정 시 예외 발생")
		@Test
		void fail_duplicatePhone() {
			UserProfile user = UserFixture.createActiveUser();
			UserUpdateRequest req = new UserUpdateRequest(
				"새로운닉네임", "010-9999-9999", true, "new/image.jpg"
			);
			String duplicatePhone = req.getNormalizedPhone();

			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
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
			UserProfile user = UserFixture.createActiveUser();

			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

			MyInfoResponse res = userService.getMyInfo(user.getId());

			assertThat(res.userId()).isEqualTo(user.getId());
			assertThat(res.email()).isEqualTo(user.getEmail());
			assertThat(res.name()).isEqualTo(user.getName());
			assertThat(res.nickname()).isEqualTo(user.getNickname());
			assertThat(res.phone()).isEqualTo(user.getPhone());
			assertThat(res.profileImageUrl()).isEqualTo(user.getProfileImageUrl());
			assertThat(res.smsAgree()).isEqualTo(user.isSmsAgree());

			verify(userRepository, times(1)).findById(user.getId());
		}

		@DisplayName("존재하지 않는 회원 조회 시 예외 발생")
		@Test
		void fail_userNotFound() {
			given(userRepository.findById(anyLong())).willReturn(Optional.empty());

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
			UserProfile user = UserFixture.createActiveUser();
			given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

			UserInfoResponse res = userService.getUserInfo(user.getId());

			assertThat(res.userId()).isEqualTo(user.getId());
			assertThat(res.nickname()).isEqualTo(user.getNickname());
			assertThat(res.profileImageUrl()).isEqualTo(user.getProfileImageUrl());

			verify(userRepository, times(1)).findById(user.getId());
		}

		@DisplayName("존재하지 않는 회원 조회 시 예외 발생")
		@Test
		void fail_userNotFound() {
			given(userRepository.findById(anyLong())).willReturn(Optional.empty());

			BizException exception = assertThrows(BizException.class, () -> userService.getUserInfo(99L));

			assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
		}
	}
}