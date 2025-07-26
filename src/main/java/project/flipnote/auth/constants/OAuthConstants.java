package project.flipnote.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OAuthConstants {
	public static final String VERIFIER_COOKIE_NAME = "oauth2_auth_request";
	public static final int VERIFIER__COOKIE_MAX_AGE = 180;
}
