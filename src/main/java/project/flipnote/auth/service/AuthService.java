package project.flipnote.auth.service;

import java.util.List;

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
import project.flipnote.auth.model.event.EmailVerificationSendEvent;
import project.flipnote.auth.model.event.PasswordResetCreateEvent;
import project.flipnote.auth.model.request.ChangePasswordRequest;
import project.flipnote.auth.model.request.EmailVerificationRequest;
import project.flipnote.auth.model.request.EmailVerifyRequest;
import project.flipnote.auth.model.request.PasswordResetCreateRequest;
import project.flipnote.auth.model.request.PasswordResetRequest;
import project.flipnote.auth.model.vo.TokenPair;
import project.flipnote.auth.model.request.UserLoginRequest;
import project.flipnote.auth.model.request.UserRegisterRequest;
import project.flipnote.auth.model.response.UserRegisterResponse;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.auth.repository.OAuthLinkRepository;
import project.flipnote.auth.repository.PasswordResetRedisRepository;
import project.flipnote.auth.repository.TokenBlacklistRedisRepository;
import project.flipnote.auth.repository.UserAuthRepository;
import project.flipnote.auth.util.PasswordResetTokenGenerator;
import project.flipnote.auth.util.VerificationCodeGenerator;
import project.flipnote.common.config.ClientProperties;
import project.flipnote.common.model.event.UserRegisteredEvent;
import project.flipnote.common.model.request.UserCreateCommand;
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
	private final AuthPolicyService authPolicyService;
	private final AuthReader authReader;

	@Transactional
	public UserRegisterResponse register(UserRegisterRequest req) {
		String email = req.email();

		authPolicyService.validateEmailDuplicate(email);
		authPolicyService.validateEmailVerified(email);

		UserCreateCommand command = req.toCommand();
		Long userId = userService.createUser(command);

		UserAuth userAuth = UserAuth.builder()
			.email(email)
			.password(passwordEncoder.encode(req.password()))
			.userId(userId)
			.build();
		userAuthRepository.save(userAuth);

		eventPublisher.publishEvent(new UserRegisteredEvent(email, userId));

		return UserRegisterResponse.from(userId);
	}

	public TokenPair login(UserLoginRequest req) {
		UserAuth userAuth = authReader.findActiveAuthAccountByEmail(req.email());

		authPolicyService.validatePasswordMatch(req.password(), userAuth.getPassword());

		return jwtComponent.generateTokenPair(userAuth);
	}

	public void sendEmailVerificationCode(EmailVerificationRequest req) {
		String email = req.email();

		authPolicyService.validateEmailDuplicate(email);
		authPolicyService.validateVerificationCodeNotExists(email);

		String code = verificationCodeGenerator.generateVerificationCode(VerificationConstants.CODE_LENGTH);

		emailVerificationRedisRepository.saveCode(email, code);

		eventPublisher.publishEvent(new EmailVerificationSendEvent(email, code));
	}

	public void verifyEmail(EmailVerifyRequest req) {
		String email = req.email();

		String code = authReader.findVerificationCodeOrThrow(email);

		authPolicyService.validateVerificationCode(req.code(), code);

		emailVerificationRedisRepository.deleteCode(email);
		emailVerificationRedisRepository.markAsVerified(email);
	}

	public TokenPair refreshToken(String refreshToken) {
		authPolicyService.validateRefreshTokenExists(refreshToken);

		long expirationMillis = jwtComponent.getExpirationMillis(refreshToken);
		tokenBlacklistRedisRepository.save(refreshToken, expirationMillis);

		AuthPrinciple userAuth = jwtComponent.extractUserAuthFromToken(refreshToken);

		return jwtComponent.generateTokenPair(userAuth);
	}

	public void requestPasswordReset(PasswordResetCreateRequest req) {
		String email = req.email();

		authPolicyService.validatePasswordResetTokenNotExists(email);

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

		String email = authReader.findActivePasswordResetToken(token);

		String encodedPassword = passwordEncoder.encode(req.password());
		userAuthRepository.updatePassword(email, encodedPassword);

		passwordResetRedisRepository.deleteToken(email, token);
	}

	@Transactional
	public void changePassword(Long authId, ChangePasswordRequest req) {
		UserAuth userAuth = authReader.findActiveAuthAccount(authId);

		authPolicyService.validatePasswordMatch(req.currentPassword(), userAuth.getPassword());

		userAuth.changePassword(passwordEncoder.encode(req.newPassword()));

		tokenVersionService.incrementTokenVersion(authId);
	}

	public SocialLinksResponse getSocialLinks(Long authId) {
		List<OAuthLink> links = oAuthLinkRepository.findByUserAuth_Id(authId);

		return SocialLinksResponse.from(links);
	}

	@Transactional
	public void deleteSocialLink(Long authId, Long socialLinkId) {
		authPolicyService.validateSocialLinkExists(socialLinkId, authId);

		oAuthLinkRepository.deleteById(socialLinkId);
	}
}
