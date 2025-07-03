package project.flipnote.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.user.model.UserRegisterDto;
import project.flipnote.user.entity.User;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserRegisterDto.Response register(UserRegisterDto.Request req) {
		validateEmailDuplicate(req.email());
		validatePhoneDuplicate(req.phone());

		User user = User.builder()
			.email(req.email())
			.password(passwordEncoder.encode(req.password()))
			.name(req.name())
			.nickname(req.nickname())
			.smsAgree(req.smsAgree())
			.phone(req.phone())
			.profileImageUrl(req.profileImageUrl())
			.build();
		userRepository.save(user);

		return UserRegisterDto.Response.from(user.getId());
	}

	private void validateEmailDuplicate(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new BizException(UserErrorCode.DUPLICATE_EMAIL);
		}
	}

	private void validatePhoneDuplicate(String phone) {
		if (userRepository.existsByPhone(phone)) {
			throw  new BizException(UserErrorCode.DUPLICATE_PHONE);
		}
	}
}
