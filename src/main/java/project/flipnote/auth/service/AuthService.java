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
import project.flipnote.auth.model.EmailVerificationConfirmDto;
import project.flipnote.auth.model.EmailVerificationDto;
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

		if (!passwordEncoder.matches(req.password(), user.getPassword())) {
			throw new BizException(AuthErrorCode.INVALID_CREDENTIALS);
		}

		return jwtComponent.generateTokenPair(user.getEmail(), user.getId(), user.getRole().name());
	}

	private User findByEmailOrThrow(UserLoginDto.Request req) {
		return userRepository.findByEmail(req.email())
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_CREDENTIALS));
	}

	public void sendEmailVerificationCode(EmailVerificationDto.Request req) {
		final String email = req.email();

		if (emailVerificationRedisRepository.existCode(email)) {
			throw new BizException(AuthErrorCode.ALREADY_ISSUED_VERIFICATION_CODE);
		}

		final String code = generateVerificationCode(VerificationConstants.CODE_LENGTH);

		emailVerificationRedisRepository.saveCode(email, code);

		eventPublisher.publishEvent(new EmailVerificationSendEvent(email, code));
	}

	public void confirmEmailVerificationCode(EmailVerificationConfirmDto.Request req) {
		String email = req.email();

		String code = emailVerificationRedisRepository.findCode(email)
			.orElseThrow(() -> new BizException(AuthErrorCode.NOT_ISSUED_VERIFICATION_CODE));

		if (!Objects.equals(req.code(), code)) {
			throw new BizException(AuthErrorCode.INVALID_VERIFICATION_CODE);
		}

		emailVerificationRedisRepository.deleteCode(email);
		emailVerificationRedisRepository.markAsVerified(email);
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
