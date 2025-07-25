package project.flipnote.auth.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.AuthRedisKey;

@RequiredArgsConstructor
@Repository
public class PasswordResetRedisRepository {

	private final StringRedisTemplate stringRedisTemplate;

	public void saveToken(String email, String token) {
		String key = AuthRedisKey.PASSWORD_RESET_TOKEN.key(token);
		Duration ttl = AuthRedisKey.PASSWORD_RESET_TOKEN.getTtl();

		stringRedisTemplate.opsForValue().set(key, email, ttl);
	}

	public Optional<String> findEmail(String token) {
		String key = AuthRedisKey.PASSWORD_RESET_TOKEN.key(token);

		String email = stringRedisTemplate.opsForValue().get(key);

		return Optional.ofNullable(email);
	}

	public void deleteToken(String token) {
		String key = AuthRedisKey.PASSWORD_RESET_TOKEN.key(token);

		stringRedisTemplate.delete(key);
	}

	public void saveEmail(String email) {
		String key = AuthRedisKey.PASSWORD_RESET_EMAIL.key(email);
		Duration ttl = AuthRedisKey.PASSWORD_RESET_EMAIL.getTtl();

		stringRedisTemplate.opsForValue().set(key, "1", ttl);
	}

	public boolean hasActiveToken(String email) {
		String key = AuthRedisKey.PASSWORD_RESET_EMAIL.key(email);

		return stringRedisTemplate.hasKey(key);
	}
}
