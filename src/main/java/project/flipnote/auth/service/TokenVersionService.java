package project.flipnote.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.repository.TokenVersionRedisRepository;
import project.flipnote.auth.repository.UserAuthRepository;

@RequiredArgsConstructor
@Service
public class TokenVersionService {

	private final TokenVersionRedisRepository tokenVersionRedisRepository;
	private final UserAuthRepository userAuthRepository;

	public Optional<Long> findTokenVersion(long authId) {
		return tokenVersionRedisRepository.getTokenVersion(authId)
			.or(() -> {
				Optional<Long> dbTokenVersion = userAuthRepository.findTokenVersionById(authId);
				dbTokenVersion.ifPresent(
					tokenVersion -> tokenVersionRedisRepository.saveTokenVersion(authId, tokenVersion)
				);
				return dbTokenVersion;
			});
	}

	@Transactional
	public void incrementTokenVersion(long authId) {
		userAuthRepository.incrementTokenVersion(authId);
		tokenVersionRedisRepository.deleteTokenVersion(authId);
	}
}
