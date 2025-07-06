package project.flipnote.user.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.service.AuthService;
import project.flipnote.common.exception.BizException;
import project.flipnote.user.entity.User;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.UserRegisterDto;
import project.flipnote.user.repository.UserRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthService authService;

	@Transactional
	public UserRegisterDto.Response register(UserRegisterDto.Request req) {
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

		return UserRegisterDto.Response.from(savedUser.getId());
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
