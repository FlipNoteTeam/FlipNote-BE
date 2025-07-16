package project.flipnote.user.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.repository.TokenVersionRedisRepository;
import project.flipnote.auth.service.AuthService;
import project.flipnote.common.exception.BizException;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
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
	private final AuthService authService;
	private final TokenVersionRedisRepository tokenVersionRedisRepository;

	@Transactional
	public UserRegisterResponse register(UserRegisterRequest req) {
		String email = req.email();
		String phone = req.getCleanedPhone();

		validateEmailDuplicate(email);
		validatePhoneDuplicate(phone);

		authService.validateEmail(email);

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

		authService.deleteVerifiedEmail(email);

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

		String phone = req.getCleanedPhone();
		validatePhoneDuplicate(phone);

		user.update(req.nickname(), phone, req.smsAgree(), req.profileImageUrl());

		return UserUpdateResponse.from(user);
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
