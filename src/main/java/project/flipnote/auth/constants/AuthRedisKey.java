package project.flipnote.auth.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.flipnote.common.constants.RedisKeys;

@Getter
@AllArgsConstructor
public enum AuthRedisKey implements RedisKeys {
	EMAIL_CODE("auth:email:code:%s", VerificationConstants.CODE_TTL_MINUTES * 60),
	EMAIL_VERIFIED("auth:email:verified:%s", 600),
	TOKEN_VERSION("auth:token:version:%d", 3600),
	;

	private final String pattern;
	private final int ttlSeconds;
}
