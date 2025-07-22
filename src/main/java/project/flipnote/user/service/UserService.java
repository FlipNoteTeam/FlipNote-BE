package project.flipnote.user.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.auth.repository.TokenVersionRedisRepository;
import project.flipnote.auth.service.AuthService;
import project.flipnote.auth.service.TokenVersionService;
import project.flipnote.common.exception.BizException;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.ChangePasswordRequest;
import project.flipnote.user.model.UserInfoResponse;
import project.flipnote.user.model.UserRegisterRequest;
import project.flipnote.user.model.UserRegisterResponse;
import project.flipnote.user.model.UserUpdateRequest;
import project.flipnote.user.model.UserUpdateResponse;
import project.flipnote.user.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenVersionRedisRepository tokenVersionRedisRepository;
	private final EmailVerificationRedisRepository emailVerificationRedisRepository;
	private final AuthService authService;
	private final TokenVersionService tokenVersionService;

	@Transactional
	public UserRegisterResponse register(UserRegisterRequest req) {
		String email = req.email();
		String phone = req.getNormalizedPhone();

		validateEmailDuplicate(email);
		validatePhoneDuplicate(phone);

		if (!emailVerificationRedisRepository.isVerified(email)) {
			throw new BizException(UserErrorCode.UNVERIFIED_EMAIL);
		}

		User user = User.builder()
			.email(email)
			.password(passwordEncoder.encode(req.password()))
			.name(req.name())
			.nickname(req.nickname())
			.smsAgree(req.smsAgree())
			.phone(phone)
			.profileImageUrl(req.profileImageUrl())
			.build();
		User savedUser = userRepository.save(user);

		emailVerificationRedisRepository.deleteVerified(email);

		return UserRegisterResponse.from(savedUser.getId());
	}

	@Transactional
	public void unregister(Long userId) {
		User user = findActiveUserById(userId);

		user.unregister();
		tokenVersionRedisRepository.deleteTokenVersion(userId);
	}

	@Transactional
	public UserUpdateResponse update(Long userId, UserUpdateRequest req) {
		User user = findActiveUserById(userId);

		String phone = req.getNormalizedPhone();
		if (!Objects.equals(user.getPhone(), phone)) {
			validatePhoneDuplicate(phone);
		}

		user.update(req.nickname(), phone, req.smsAgree(), req.profileImageUrl());

		return UserUpdateResponse.from(user);
	}

	public MyInfoResponse getMyInfo(Long userId) {
		User user = findActiveUserById(userId);

		return MyInfoResponse.from(user);
	}

	public UserInfoResponse getUserInfo(Long userId) {
		User user = findActiveUserById(userId);

		return UserInfoResponse.from(user);
	}

	@Transactional
	public void changePassword(Long userId, ChangePasswordRequest req) {
		User user = findActiveUserById(userId);

		authService.validatePasswordMatch(req.currentPassword(), user.getPassword());

		user.changePassword(passwordEncoder.encode(req.newPassword()));

		tokenVersionService.incrementTokenVersion(userId);
	}

	private User findActiveUserById(Long userId) {
		return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
			.orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
	}

	private void validateEmailDuplicate(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new BizException(UserErrorCode.DUPLICATE_EMAIL);
		}
	}

	private void validatePhoneDuplicate(String phone) {
		if (Objects.isNull(phone)) {
			return;
		}

		if (userRepository.existsByPhone(phone)) {
			throw new BizException(UserErrorCode.DUPLICATE_PHONE);
		}
	}
}
