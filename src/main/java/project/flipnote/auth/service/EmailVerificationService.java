package project.flipnote.auth.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.user.exception.UserErrorCode;

@RequiredArgsConstructor
@Service
public class EmailVerificationService {

	private final EmailVerificationRedisRepository emailVerificationRedisRepository;

	public void validateVerified(String email) {
		if (!emailVerificationRedisRepository.isVerified(email)) {
			throw new BizException(UserErrorCode.UNVERIFIED_EMAIL);
		}
	}
}
