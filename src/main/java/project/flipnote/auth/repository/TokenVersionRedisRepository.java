package project.flipnote.auth.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.AuthRedisKey;

@RequiredArgsConstructor
@Repository
public class TokenVersionRedisRepository {

	private final RedisTemplate<String, Long> tokenVersionRedisTemplate;

	public void saveTokenVersion(long accountId, long tokenVersion) {
		String key = AuthRedisKey.TOKEN_VERSION.key(accountId);
		Duration ttl = AuthRedisKey.TOKEN_VERSION.getTtl();

		tokenVersionRedisTemplate.opsForValue().set(key, tokenVersion, ttl);
	}

	public Optional<Long> getTokenVersion(long accountId) {
		String key = AuthRedisKey.TOKEN_VERSION.key(accountId);
		Long value = tokenVersionRedisTemplate.opsForValue().get(key);

		return Optional.ofNullable(value);
	}

	public void deleteTokenVersion(long accountId) {
		String key = AuthRedisKey.TOKEN_VERSION.key(accountId);

		tokenVersionRedisTemplate.delete(key);
	}
}
