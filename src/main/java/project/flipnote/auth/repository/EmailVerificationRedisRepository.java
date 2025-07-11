package project.flipnote.auth.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.AuthRedisKey;

@RequiredArgsConstructor
@Repository
public class EmailVerificationRedisRepository {

	private final RedisTemplate<String, String> emailRedisTemplate;

	public void saveCode(String email, String code) {
		String key = AuthRedisKey.EMAIL_CODE.key(email);
		Duration ttl = AuthRedisKey.EMAIL_CODE.getTtl();

		emailRedisTemplate.opsForValue().set(key, code, ttl);
	}

	public boolean existCode(String email) {
		String key = AuthRedisKey.EMAIL_CODE.key(email);

		return emailRedisTemplate.hasKey(key);
	}

	public Optional<String> findCode(String email) {
		String key = AuthRedisKey.EMAIL_CODE.key(email);

		String code = emailRedisTemplate.opsForValue().get(key);

		return Optional.ofNullable(code);
	}

	public void deleteCode(String email) {
		String key = AuthRedisKey.EMAIL_CODE.key(email);

		emailRedisTemplate.delete(key);
	}

	public void markAsVerified(String email) {
		String key = AuthRedisKey.EMAIL_VERIFIED.key(email);
		Duration ttl = AuthRedisKey.EMAIL_VERIFIED.getTtl();

		emailRedisTemplate.opsForValue().set(key, "1", ttl);
	}

	public boolean isVerified(String email) {
		String key = AuthRedisKey.EMAIL_VERIFIED.key(email);

		return emailRedisTemplate.hasKey(key);
	}

	public void deleteVerified(String email) {
		String key = AuthRedisKey.EMAIL_VERIFIED.key(email);

		emailRedisTemplate.delete(key);
	}
}
