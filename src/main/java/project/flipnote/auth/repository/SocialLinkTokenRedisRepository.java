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

	public void saveToken(long userId, String token) {
		String key = AuthRedisKey.SOCIAL_LINK_TOKEN.key(token);
		Duration ttl = AuthRedisKey.SOCIAL_LINK_TOKEN.getTtl();

		stringRedisTemplate.opsForValue().set(key, String.valueOf(userId), ttl);
	}

	public Optional<Long> findUserIdByToken(String token) {
		String key = AuthRedisKey.SOCIAL_LINK_TOKEN.key(token);

		String userId = stringRedisTemplate.opsForValue().get(key);

		return Optional.ofNullable(userId)
			.map(Long::parseLong);
	}

	public void deleteToken(String token) {
		String key = AuthRedisKey.SOCIAL_LINK_TOKEN.key(token);
	}
}
