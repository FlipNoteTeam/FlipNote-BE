package project.flipnote.user.service;

import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.dto.UserCreateCommand;
import project.flipnote.common.event.UserWithdrawnEvent;
import project.flipnote.common.exception.BizException;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.UserInfoResponse;
import project.flipnote.user.model.UserUpdateRequest;
import project.flipnote.user.model.UserUpdateResponse;
import project.flipnote.user.repository.UserProfileRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

	private final UserProfileRepository userProfileRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public Long createUser(UserCreateCommand command) {
		validateEmailDuplicate(command.email());
		validatePhoneDuplicate(command.phone());

		UserProfile user = UserProfile.builder()
			.email(command.email())
			.name(command.name())
			.nickname(command.nickname())
			.profileImageUrl(command.profileImageUrl())
			.phone(command.phone())
			.smsAgree(command.smsAgree())
			.build();

		UserProfile savedUser = userProfileRepository.save(user);
		return savedUser.getId();
	}

	@Transactional
	public void withdraw(Long userId) {
		UserProfile user = findActiveUserById(userId);
		user.withdraw();

		eventPublisher.publishEvent(new UserWithdrawnEvent(userId));
	}

	@Transactional
	public UserUpdateResponse update(Long userId, UserUpdateRequest req) {
		UserProfile user = findActiveUserById(userId);

		String phone = req.getNormalizedPhone();
		if (!Objects.equals(user.getPhone(), phone)) {
			validatePhoneDuplicate(phone);
		}

		user.update(req.nickname(), phone, req.smsAgree(), req.profileImageUrl());

		return UserUpdateResponse.from(user);
	}

	public MyInfoResponse getMyInfo(Long userId) {
		UserProfile user = findActiveUserById(userId);

		return MyInfoResponse.from(user);
	}

	public UserInfoResponse getUserInfo(Long userId) {
		UserProfile user = findActiveUserById(userId);

		return UserInfoResponse.from(user);
	}

	private UserProfile findActiveUserById(Long userId) {
		return userProfileRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
			.orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
	}

	private void validateEmailDuplicate(String email) {
		if (userProfileRepository.existsByEmail(email)) {
			throw new BizException(UserErrorCode.DUPLICATE_EMAIL);
		}
	}

	public void validatePhoneDuplicate(String phone) {
		if (Objects.isNull(phone)) {
			return;
		}

		if (userProfileRepository.existsByPhone(phone)) {
			throw new BizException(UserErrorCode.DUPLICATE_PHONE);
		}
	}
}
