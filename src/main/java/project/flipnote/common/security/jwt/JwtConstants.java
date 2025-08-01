package project.flipnote.common.security.jwt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtConstants {

	public static final String REFRESH_TOKEN = "refreshToken";

	public static final String ROLE = "role";
	public static final String TOKEN_VERSION = "token_version";
	public static final String USER_ID = "user_id";

	public static final String AUTH_HEADER = "Authorization";
	public static final String TOKEN_PREFIX = "Bearer ";
}
