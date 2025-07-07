package project.flipnote.auth.service;

import java.security.SecureRandom;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.VerificationConstants;
import project.flipnote.auth.event.EmailVerificationSendEvent;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.model.EmailVerificationConfirmRequest;
import project.flipnote.auth.model.EmailVerificationRequest;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.model.UserLoginDto;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.jwt.JwtComponent;
import project.flipnote.user.entity.User;
import project.flipnote.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtComponent jwtComponent;
	private final EmailVerificationRedisRepository emailVerificationRedisRepository;
	private final ApplicationEventPublisher eventPublisher;

	private static final SecureRandom random = new SecureRandom();

	public TokenPair login(UserLoginDto.Request req) {
		User user = findByEmailOrThrow(req);

		validatePasswordMatch(req.password(), user.getPassword());

		return jwtComponent.generateTokenPair(user.getEmail(), user.getId(), user.getRole().name());
	}

	private User findByEmailOrThrow(UserLoginDto.Request req) {
		return userRepository.findByEmail(req.email())
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_CREDENTIALS));
	}

	private void validatePasswordMatch(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BizException(AuthErrorCode.INVALID_CREDENTIALS);
		}
	}

	public void sendEmailVerificationCode(EmailVerificationRequest req) {
		final String email = req.email();

		validateEmailIsAvailable(email);
		validateVerificationCodeNotExists(email);

		final String code = generateVerificationCode(VerificationConstants.CODE_LENGTH);

		emailVerificationRedisRepository.saveCode(email, code);

		eventPublisher.publishEvent(new EmailVerificationSendEvent(email, code));
	}

	private void validateEmailIsAvailable(String email) {
		if (userRepository.existsByEmail(email)) {
			throw new BizException(AuthErrorCode.EXISTING_EMAIL);
		}
	}

	private void validateVerificationCodeNotExists(String email) {
		if (emailVerificationRedisRepository.existCode(email)) {
			throw new BizException(AuthErrorCode.ALREADY_ISSUED_VERIFICATION_CODE);
		}
	}

	public void confirmEmailVerificationCode(EmailVerificationConfirmRequest req) {
		String email = req.email();

		String code = findVerificationCodeOrThrow(email);

		validateVerificationCode(req.code(), code);

		emailVerificationRedisRepository.deleteCode(email);
		emailVerificationRedisRepository.markAsVerified(email);
	}

	private String findVerificationCodeOrThrow(String email) {
		return emailVerificationRedisRepository.findCode(email)
			.orElseThrow(() -> new BizException(AuthErrorCode.NOT_ISSUED_VERIFICATION_CODE));
	}

	private void validateVerificationCode(String inputCode, String savedCode) {
		if (!Objects.equals(inputCode, savedCode)) {
			throw new BizException(AuthErrorCode.INVALID_VERIFICATION_CODE);
		}
	}

	public void validateEmail(String email) {
		if (!emailVerificationRedisRepository.isVerified(email)) {
			throw new BizException(AuthErrorCode.UNVERIFIED_EMAIL);
		}
	}

	public void deleteVerifiedEmail(String email) {
		emailVerificationRedisRepository.deleteVerified(email);
	}

	private String generateVerificationCode(int length) {
		int origin = (int)Math.pow(10, length - 1);
		int bound = (int)Math.pow(10, length);
		return String.valueOf(random.nextInt(origin, bound));
	}
}
