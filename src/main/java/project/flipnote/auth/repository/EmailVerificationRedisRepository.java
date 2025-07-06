package project.flipnote.auth.repository;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class EmailVerificationRedisRepository {

	private final RedisTemplate<String, String> emailRedisTemplate;

	public void saveCode(String email, String code, int ttl) {
		emailRedisTemplate.opsForValue().set(email, code, Duration.ofMinutes(ttl));
	}

	public boolean existCode(String email) {
		return emailRedisTemplate.hasKey(email);
	}
}
