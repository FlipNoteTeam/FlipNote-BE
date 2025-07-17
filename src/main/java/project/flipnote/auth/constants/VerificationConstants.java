package project.flipnote.auth.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VerificationConstants {
	public static final int CODE_LENGTH = 6;
	public static final int CODE_TTL_MINUTES = 5;
}
