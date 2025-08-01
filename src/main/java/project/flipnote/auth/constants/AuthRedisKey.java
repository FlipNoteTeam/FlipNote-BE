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
	TOKEN_BLACKLIST("auth:token:blacklist:%s", -1),
	PASSWORD_RESET_TOKEN("auth:password_reset:token:%s", PasswordResetConstants.TOKEN_TTL_MINUTES * 60),
	PASSWORD_RESET_EMAIL("auth:password_reset:email:%s", PasswordResetConstants.TOKEN_TTL_MINUTES * 60),
	SOCIAL_LINK_TOKEN("auth:social:link_token:%s", 180);

	private final String pattern;
	private final int ttlSeconds;
}
