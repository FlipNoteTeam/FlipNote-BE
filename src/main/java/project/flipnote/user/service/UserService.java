package project.flipnote.user.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.auth.repository.TokenVersionRedisRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.MyInfoResponse;
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

	@Transactional
	public UserRegisterResponse register(UserRegisterRequest req) {
		String email = req.email();
		String phone = req.getNormalizedPhone();

		validateEmailDuplicate(email);
		validatePhoneDuplicate(phone);

		if (!emailVerificationRedisRepository.isVerified(email)) {
			throw new BizException(UserErrorCode.UNVERIFIED_EMAIL);
		}

		UserProfile userProfile = UserProfile.builder()
			.email(email)
			.password(passwordEncoder.encode(req.password()))
			.name(req.name())
			.nickname(req.nickname())
			.smsAgree(req.smsAgree())
			.phone(phone)
			.profileImageUrl(req.profileImageUrl())
			.build();
		UserProfile savedUserProfile = userRepository.save(userProfile);

		emailVerificationRedisRepository.deleteVerified(email);

		return UserRegisterResponse.from(savedUserProfile.getId());
	}

	@Transactional
	public void unregister(Long userId) {
		UserProfile userProfile = findActiveUserById(userId);

		userProfile.unregister();
		tokenVersionRedisRepository.deleteTokenVersion(userId);
	}

	@Transactional
	public UserUpdateResponse update(Long userId, UserUpdateRequest req) {
		UserProfile userProfile = findActiveUserById(userId);

		String phone = req.getNormalizedPhone();
		if (!Objects.equals(userProfile.getPhone(), phone)) {
			validatePhoneDuplicate(phone);
		}

		userProfile.update(req.nickname(), phone, req.smsAgree(), req.profileImageUrl());

		return UserUpdateResponse.from(userProfile);
	}

	public MyInfoResponse getMyInfo(Long userId) {
		UserProfile userProfile = findActiveUserById(userId);

		return MyInfoResponse.from(userProfile);
	}

	public UserInfoResponse getUserInfo(Long userId) {
		UserProfile userProfile = findActiveUserById(userId);

		return UserInfoResponse.from(userProfile);
	}

	private UserProfile findActiveUserById(Long userId) {
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
