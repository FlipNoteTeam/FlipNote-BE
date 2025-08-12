package project.flipnote.auth.service;

import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.constants.VerificationConstants;
import project.flipnote.auth.entity.AccountStatus;
import project.flipnote.auth.entity.OAuthLink;
import project.flipnote.auth.entity.UserAuth;
import project.flipnote.auth.event.EmailVerificationSendEvent;
import project.flipnote.auth.event.PasswordResetCreateEvent;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.model.ChangePasswordRequest;
import project.flipnote.auth.model.EmailVerifyRequest;
import project.flipnote.auth.model.EmailVerificationRequest;
import project.flipnote.auth.model.PasswordResetCreateRequest;
import project.flipnote.auth.model.PasswordResetRequest;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.model.UserLoginRequest;
import project.flipnote.auth.model.UserRegisterRequest;
import project.flipnote.auth.model.UserRegisterResponse;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.auth.repository.OAuthLinkRepository;
import project.flipnote.auth.repository.PasswordResetRedisRepository;
import project.flipnote.auth.repository.TokenBlacklistRedisRepository;
import project.flipnote.auth.repository.UserAuthRepository;
import project.flipnote.auth.util.PasswordResetTokenGenerator;
import project.flipnote.auth.util.VerificationCodeGenerator;
import project.flipnote.common.config.ClientProperties;
import project.flipnote.common.dto.UserCreateCommand;
import project.flipnote.common.event.UserRegisteredEvent;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.common.security.jwt.JwtComponent;
import project.flipnote.user.model.SocialLinksResponse;
import project.flipnote.user.service.UserService;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

	private final JwtComponent jwtComponent;
	private final EmailVerificationRedisRepository emailVerificationRedisRepository;
	private final TokenBlacklistRedisRepository tokenBlacklistRedisRepository;
	private final PasswordEncoder passwordEncoder;
	private final ApplicationEventPublisher eventPublisher;
	private final VerificationCodeGenerator verificationCodeGenerator;
	private final PasswordResetTokenGenerator passwordResetTokenGenerator;
	private final PasswordResetRedisRepository passwordResetRedisRepository;
	private final ClientProperties clientProperties;
	private final UserService userService;
	private final UserAuthRepository userAuthRepository;
	private final TokenVersionService tokenVersionService;
	private final OAuthLinkRepository oAuthLinkRepository;

	@Transactional
	public UserRegisterResponse register(UserRegisterRequest req) {
		String email = req.email();

		validateEmailDuplicate(email);
		validateEmailVerified(email);

		UserCreateCommand command = req.toCommand();
		Long userId = userService.createUser(command);

		UserAuth userAuth = UserAuth.builder()
			.email(email)
			.password(passwordEncoder.encode(req.password()))
			.userId(userId)
			.build();
		userAuthRepository.save(userAuth);

		eventPublisher.publishEvent(new UserRegisteredEvent(userId, email));

		return UserRegisterResponse.from(userId);
	}

	public TokenPair login(UserLoginRequest req) {
		UserAuth userAuth = findActiveAuthAccountByEmail(req.email());

		validatePasswordMatch(req.password(), userAuth.getPassword());

		return jwtComponent.generateTokenPair(userAuth);
	}

	public void sendEmailVerificationCode(EmailVerificationRequest req) {
		String email = req.email();

		validateEmailDuplicate(email);
		validateVerificationCodeNotExists(email);

		String code = verificationCodeGenerator.generateVerificationCode(VerificationConstants.CODE_LENGTH);

		emailVerificationRedisRepository.saveCode(email, code);

		eventPublisher.publishEvent(new EmailVerificationSendEvent(email, code));
	}

	public void verifyEmail(EmailVerifyRequest req) {
		String email = req.email();

		String code = findVerificationCodeOrThrow(email);

		validateVerificationCode(req.code(), code);

		emailVerificationRedisRepository.deleteCode(email);
		emailVerificationRedisRepository.markAsVerified(email);
	}

	public TokenPair refreshToken(String refreshToken) {
		if (tokenBlacklistRedisRepository.exist(refreshToken)) {
			throw new BizException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}

		long expirationMillis = jwtComponent.getExpirationMillis(refreshToken);
		tokenBlacklistRedisRepository.save(refreshToken, expirationMillis);

		AuthPrinciple userAuth = jwtComponent.extractUserAuthFromToken(refreshToken);

		return jwtComponent.generateTokenPair(userAuth);
	}

	public void requestPasswordReset(PasswordResetCreateRequest req) {
		String email = req.email();
		if (passwordResetRedisRepository.hasActiveToken(email)) {
			throw new BizException(AuthErrorCode.ALREADY_SENT_PASSWORD_RESET_LINK);
		}

		boolean existUser = userAuthRepository.existsByEmailAndStatus(email, AccountStatus.ACTIVE);
		if (existUser) {
			String token = passwordResetTokenGenerator.generateToken();
			passwordResetRedisRepository.saveToken(email, token);

			String link = clientProperties.buildUrl(ClientProperties.PathKey.PASSWORD_RESET, token);
			eventPublisher.publishEvent(new PasswordResetCreateEvent(email, link));
		}
	}

	@Transactional
	public void resetPassword(PasswordResetRequest req) {
		String token = req.token();

		String email = passwordResetRedisRepository.findEmailByToken(token)
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_PASSWORD_RESET_TOKEN));

		String encodedPassword = passwordEncoder.encode(req.password());
		userAuthRepository.updatePassword(email, encodedPassword);

		passwordResetRedisRepository.deleteToken(email, token);
	}

	@Transactional
	public void changePassword(Long authId, ChangePasswordRequest req) {
		UserAuth userAuth = findActiveAuthAccount(authId);

		validatePasswordMatch(req.currentPassword(), userAuth.getPassword());

		userAuth.changePassword(passwordEncoder.encode(req.newPassword()));

		tokenVersionService.incrementTokenVersion(authId);
	}

	public SocialLinksResponse getSocialLinks(Long authId) {
		List<OAuthLink> links = oAuthLinkRepository.findByUserAuth_Id(authId);

		return SocialLinksResponse.from(links);
	}

	@Transactional
	public void deleteSocialLink(Long authId, Long socialLinkId) {
		if (!oAuthLinkRepository.existsByIdAndUserAuth_Id(socialLinkId, authId)) {
			throw new BizException(AuthErrorCode.SOCIAL_LINK_NOT_FOUND);
		}

		oAuthLinkRepository.deleteById(socialLinkId);
	}

	private void validateEmailVerified(String email) {
		if (!emailVerificationRedisRepository.isVerified(email)) {
			throw new BizException(AuthErrorCode.UNVERIFIED_EMAIL);
		}
	}

	public void validatePasswordMatch(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BizException(AuthErrorCode.INVALID_CREDENTIALS);
		}
	}

	private UserAuth findActiveAuthAccountByEmail(String email) {
		return userAuthRepository.findByEmailAndStatus(email, AccountStatus.ACTIVE)
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_CREDENTIALS));
	}

	private UserAuth findActiveAuthAccount(Long authId) {
		return userAuthRepository.findByIdAndStatus(authId, AccountStatus.ACTIVE)
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_CREDENTIALS));
	}

	private void validateEmailDuplicate(String email) {
		if (userAuthRepository.existsByEmail(email)) {
			throw new BizException(AuthErrorCode.EXISTING_EMAIL);
		}
	}

	private void validateVerificationCodeNotExists(String email) {
		if (emailVerificationRedisRepository.existCode(email)) {
			throw new BizException(AuthErrorCode.ALREADY_ISSUED_VERIFICATION_CODE);
		}
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
}
