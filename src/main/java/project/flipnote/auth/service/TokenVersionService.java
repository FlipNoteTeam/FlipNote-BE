package project.flipnote.auth.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.repository.TokenVersionRedisRepository;
import project.flipnote.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class TokenVersionService {

	private final TokenVersionRedisRepository tokenVersionRedisRepository;
	private final UserRepository userRepository;

	public Optional<Long> findTokenVersion(long userId) {
		return tokenVersionRedisRepository.getTokenVersion(userId)
			.or(() -> {
				Optional<Long> dbTokenVersion = userRepository.findTokenVersionById(userId);
				dbTokenVersion.ifPresent(
					tokenVersion -> tokenVersionRedisRepository.saveTokenVersion(userId, tokenVersion)
				);
				return dbTokenVersion;
			});
	}
}
