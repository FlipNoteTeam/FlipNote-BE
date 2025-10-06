package project.flipnote.auth.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.entity.AccountStatus;
import project.flipnote.auth.entity.UserAuth;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.auth.repository.PasswordResetRedisRepository;
import project.flipnote.auth.repository.UserAuthRepository;
import project.flipnote.common.exception.BizException;

@RequiredArgsConstructor
@Service
public class AuthReader {

	private final UserAuthRepository userAuthRepository;
	private final EmailVerificationRedisRepository emailVerificationRedisRepository;
	private final PasswordResetRedisRepository passwordResetRedisRepository;

	public UserAuth findActiveAuthAccountByEmail(String email) {
		return userAuthRepository.findByEmailAndStatus(email, AccountStatus.ACTIVE)
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_CREDENTIALS));
	}

	public UserAuth findActiveAuthAccount(Long authId) {
		return userAuthRepository.findByIdAndStatus(authId, AccountStatus.ACTIVE)
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_CREDENTIALS));
	}

	public String findVerificationCodeOrThrow(String email) {
		return emailVerificationRedisRepository.findCode(email)
			.orElseThrow(() -> new BizException(AuthErrorCode.NOT_ISSUED_VERIFICATION_CODE));
	}

	public String findActivePasswordResetToken(String token) {
		return passwordResetRedisRepository.findEmailByToken(token)
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_PASSWORD_RESET_TOKEN));
	}
}
