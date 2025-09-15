package project.flipnote.user.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.event.UserWithdrawnEvent;
import project.flipnote.common.model.request.UserCreateCommand;
import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.entity.ReferenceType;
import project.flipnote.image.service.ImageRefService;
import project.flipnote.image.service.ImageService;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.UserIdNickname;
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
	private final ImageService imageService;
	private final ImageRefService imageRefService;

	private final static ReferenceType type = ReferenceType.USER;


	@Transactional
	public Long createUser(UserCreateCommand command) {
		validateEmailDuplicate(command.email());
		validatePhoneDuplicate(command.phone());

		UserProfile user = UserProfile.builder()
			.email(command.email())
			.name(command.name())
			.nickname(command.nickname())
			.phone(command.phone())
			.smsAgree(command.smsAgree())
			.build();

		UserProfile savedUser = userProfileRepository.save(user);
		return savedUser.getId();
	}

	@Transactional
	public void withdraw(Long userId) {
		UserProfile user = findActiveUserByIdOrThrow(userId);

		imageRefService.deleteByReferenceAndId(type, userId);

		user.withdraw();

		eventPublisher.publishEvent(new UserWithdrawnEvent(userId));
	}

	@Transactional
	public UserUpdateResponse update(Long userId, UserUpdateRequest req) {
		UserProfile user = findActiveUserByIdOrThrow(userId);

		String phone = req.getNormalizedPhone();
		if (!Objects.equals(user.getPhone(), phone)) {
			validatePhoneDuplicate(phone);
		}

		String url = imageService.changeImage(type, userId, req.imageRefId());

		user.update(req.nickname(), phone, req.smsAgree(), url);

		return UserUpdateResponse.from(user, req.imageRefId());
	}

	public MyInfoResponse getMyInfo(Long userId) {
		UserProfile user = findActiveUserByIdOrThrow(userId);

		Optional<ImageRef> imageRef = imageRefService.findByTypeAndReferenceId(type, userId);

		Long imageRedId = imageRef.isPresent() ? imageRef.get().getId() : null;

		return MyInfoResponse.from(user, imageRedId);
	}

	public UserInfoResponse getUserInfo(Long userId) {
		UserProfile user = findActiveUserByIdOrThrow(userId);

		Optional<ImageRef> imageRef = imageRefService.findByTypeAndReferenceId(type, userId);

		Long imageRedId = imageRef.isPresent() ? imageRef.get().getId() : null;


		return UserInfoResponse.from(user, imageRedId);
	}

	private UserProfile findActiveUserByIdOrThrow(Long userId) {
		return userProfileRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
			.orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
	}

	public Optional<UserProfile> findActiveUserByEmail(String email) {
		return userProfileRepository.findByEmailAndStatus(email, UserStatus.ACTIVE);
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

	public Map<Long, String> getIdAndNicknames(List<Long> inviteeUserIds) {
		if (inviteeUserIds == null || inviteeUserIds.isEmpty()) {
			return java.util.Collections.emptyMap();
		}

		List<Long> distinctIds = inviteeUserIds.stream().distinct().toList();
		List<UserIdNickname> idAndNicknames = userProfileRepository.findIdAndNicknameByIdIn(distinctIds);
		return idAndNicknames.stream()
			.collect(Collectors.toMap(
				UserIdNickname::getId,
				UserIdNickname::getNickname,
				(a, b) -> a
			));
	}

	public String getNickname(Long userId) {
		return userProfileRepository.findNicknameById(userId)
			.orElseThrow(() -> new BizException(UserErrorCode.USER_NOT_FOUND));
	}
}
