package project.flipnote.auth.repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.AuthRedisKey;

@RequiredArgsConstructor
@Repository
public class PasswordResetRedisRepository {

	private final StringRedisTemplate stringRedisTemplate;

	public void saveToken(String email, String token) {
		stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			public List<Object> execute(RedisOperations operations) throws DataAccessException {
				operations.multi();

				String tokenKey = AuthRedisKey.PASSWORD_RESET_TOKEN.key(token);
				Duration tokenTtl = AuthRedisKey.PASSWORD_RESET_TOKEN.getTtl();
				operations.opsForValue().set(tokenKey, email, tokenTtl);

				String emailKey = AuthRedisKey.PASSWORD_RESET_EMAIL.key(email);
				Duration emailTtl = AuthRedisKey.PASSWORD_RESET_EMAIL.getTtl();
				operations.opsForValue().set(emailKey, "1", emailTtl);

				return operations.exec();
			}
		});
	}

	public Optional<String> findEmailByToken(String token) {
		String key = AuthRedisKey.PASSWORD_RESET_TOKEN.key(token);

		String email = stringRedisTemplate.opsForValue().get(key);

		return Optional.ofNullable(email);
	}

	public void deleteToken(String email, String token) {
		stringRedisTemplate.execute(new SessionCallback<List<Object>>() {
			@Override
			public List<Object> execute(RedisOperations operations) throws DataAccessException {
				operations.multi();

				String tokenKey = AuthRedisKey.PASSWORD_RESET_TOKEN.key(token);
				stringRedisTemplate.delete(tokenKey);

				String emailKey = AuthRedisKey.PASSWORD_RESET_EMAIL.key(email);
				stringRedisTemplate.delete(emailKey);

				return operations.exec();
			}
		});
	}

	public boolean hasActiveToken(String email) {
		String key = AuthRedisKey.PASSWORD_RESET_EMAIL.key(email);

		return stringRedisTemplate.hasKey(key);
	}
}
