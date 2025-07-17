package project.flipnote.auth.repository;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.AuthRedisKey;

@RequiredArgsConstructor
@Repository
public class TokenBlacklistRedisRepository {

	private final StringRedisTemplate stringRedisTemplate;

	public void save(String token, long timeoutMillis) {
		String key = AuthRedisKey.TOKEN_BLACKLIST.key(token);

		stringRedisTemplate.opsForValue().set(key, "1", timeoutMillis, TimeUnit.MILLISECONDS);
	}

	public boolean exist(String token) {
		String key = AuthRedisKey.TOKEN_BLACKLIST.key(token);

		return stringRedisTemplate.hasKey(key);
	}
}
