package project.flipnote.auth.service;

import java.util.Objects;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.auth.repository.OAuthLinkRepository;
import project.flipnote.auth.repository.PasswordResetRedisRepository;
import project.flipnote.auth.repository.TokenBlacklistRedisRepository;
import project.flipnote.auth.repository.UserAuthRepository;
import project.flipnote.common.exception.BizException;

@RequiredArgsConstructor
@Service
public class AuthPolicyService {

	private final EmailVerificationRedisRepository emailVerificationRedisRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserAuthRepository userAuthRepository;
	private final OAuthLinkRepository oAuthLinkRepository;
	private final TokenBlacklistRedisRepository tokenBlacklistRedisRepository;
	private final PasswordResetRedisRepository passwordResetRedisRepository;


	public void validateEmailVerified(String email) {
		if (!emailVerificationRedisRepository.isVerified(email)) {
			throw new BizException(AuthErrorCode.UNVERIFIED_EMAIL);
		}
	}

	public void validatePasswordMatch(String rawPassword, String encodedPassword) {
		if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
			throw new BizException(AuthErrorCode.INVALID_CREDENTIALS);
		}
	}

	public void validateEmailDuplicate(String email) {
		if (userAuthRepository.existsByEmail(email)) {
			throw new BizException(AuthErrorCode.EXISTING_EMAIL);
		}
	}

	public void validateVerificationCodeNotExists(String email) {
		if (emailVerificationRedisRepository.existCode(email)) {
			throw new BizException(AuthErrorCode.ALREADY_ISSUED_VERIFICATION_CODE);
		}
	}

	public void validateVerificationCode(String inputCode, String savedCode) {
		if (!Objects.equals(inputCode, savedCode)) {
			throw new BizException(AuthErrorCode.INVALID_VERIFICATION_CODE);
		}
	}

	public void validateSocialLinkExists(Long socialLinkId, Long authId) {
		if (!oAuthLinkRepository.existsByIdAndUserAuth_Id(socialLinkId, authId)) {
			throw new BizException(AuthErrorCode.SOCIAL_LINK_NOT_FOUND);
		}
	}

	public void validateRefreshTokenExists(String refreshToken) {
		if (tokenBlacklistRedisRepository.exist(refreshToken)) {
			throw new BizException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	public void validatePasswordResetTokenNotExists(String email) {
		if (passwordResetRedisRepository.hasActiveToken(email)) {
			throw new BizException(AuthErrorCode.ALREADY_SENT_PASSWORD_RESET_LINK);
		}
	}
}
