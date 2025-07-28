package project.flipnote.auth.repository;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.AuthRedisKey;

@RequiredArgsConstructor
@Repository
public class SocialLinkTokenRedisRepository {

	private final StringRedisTemplate stringRedisTemplate;

	public void saveToken(long accountId, String token) {
		String key = AuthRedisKey.SOCIAL_LINK_TOKEN.key(token);
		Duration ttl = AuthRedisKey.SOCIAL_LINK_TOKEN.getTtl();

		stringRedisTemplate.opsForValue().set(key, String.valueOf(accountId), ttl);
	}

	public Optional<Long> findAccountIdByToken(String token) {
		String key = AuthRedisKey.SOCIAL_LINK_TOKEN.key(token);

		String accountId = stringRedisTemplate.opsForValue().get(key);

		return Optional.ofNullable(accountId)
			.map(Long::parseLong);
	}

	public void deleteToken(String token) {
		String key = AuthRedisKey.SOCIAL_LINK_TOKEN.key(token);

		stringRedisTemplate.delete(key);
	}
}
