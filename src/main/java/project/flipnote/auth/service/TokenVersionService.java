package project.flipnote.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.repository.AuthAccountRepository;
import project.flipnote.auth.repository.TokenVersionRedisRepository;

@RequiredArgsConstructor
@Service
public class TokenVersionService {

	private final TokenVersionRedisRepository tokenVersionRedisRepository;
	private final AuthAccountRepository authAccountRepository;

	public Optional<Long> findTokenVersion(long accountId) {
		return tokenVersionRedisRepository.getTokenVersion(accountId)
			.or(() -> {
				Optional<Long> dbTokenVersion = authAccountRepository.findTokenVersionById(accountId);
				dbTokenVersion.ifPresent(
					tokenVersion -> tokenVersionRedisRepository.saveTokenVersion(accountId, tokenVersion)
				);
				return dbTokenVersion;
			});
	}

	public void incrementTokenVersion(long accountId) {
		authAccountRepository.incrementTokenVersion(accountId);
		tokenVersionRedisRepository.deleteTokenVersion(accountId);
	}
}
