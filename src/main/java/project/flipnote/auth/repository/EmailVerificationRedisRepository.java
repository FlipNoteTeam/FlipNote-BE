package project.flipnote.auth.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.AuthRedisKey;

@RequiredArgsConstructor
@Repository
public class EmailVerificationRedisRepository {

	private final StringRedisTemplate stringRedisTemplate;

	public void saveCode(String email, String code) {
		String key = AuthRedisKey.EMAIL_CODE.key(email);
		Duration ttl = AuthRedisKey.EMAIL_CODE.getTtl();

		stringRedisTemplate.opsForValue().set(key, code, ttl);
	}

	public boolean existCode(String email) {
		String key = AuthRedisKey.EMAIL_CODE.key(email);

		return stringRedisTemplate.hasKey(key);
	}

	public Optional<String> findCode(String email) {
		String key = AuthRedisKey.EMAIL_CODE.key(email);

		String code = stringRedisTemplate.opsForValue().get(key);

		return Optional.ofNullable(code);
	}

	public void deleteCode(String email) {
		String key = AuthRedisKey.EMAIL_CODE.key(email);

		stringRedisTemplate.delete(key);
	}

	public void markAsVerified(String email) {
		String key = AuthRedisKey.EMAIL_VERIFIED.key(email);
		Duration ttl = AuthRedisKey.EMAIL_VERIFIED.getTtl();

		stringRedisTemplate.opsForValue().set(key, "1", ttl);
	}

	public boolean isVerified(String email) {
		String key = AuthRedisKey.EMAIL_VERIFIED.key(email);

		return stringRedisTemplate.hasKey(key);
	}

	public void deleteVerified(String email) {
		String key = AuthRedisKey.EMAIL_VERIFIED.key(email);

		stringRedisTemplate.delete(key);
	}
}
