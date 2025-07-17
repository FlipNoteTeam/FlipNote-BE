package project.flipnote.common.util;

import org.springframework.http.ResponseCookie;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtil {

	public static ResponseCookie createCookie(
		String name,
		String value,
		int maxAge,
		boolean httpOnly,
		String path
	) {
		return ResponseCookie.from(name, value)
			.maxAge(maxAge)
			.httpOnly(httpOnly)
			.path(path)
			.build();
	}

	public static ResponseCookie createCookie(String name, String value, int maxAge) {
		return createCookie(name, value, maxAge, true, "/");
	}

	public static ResponseCookie createExpiredCookie(String name) {
		return createCookie(name, "", 0, true, "/");
	}
}
