package project.flipnote.common.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

	public ResponseCookie createCookie(
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

	public ResponseCookie createCookie(String name, String value, int maxAge) {
		return createCookie(name, value, maxAge, true, "/");
	}

	public ResponseCookie createExpiredCookie(String name) {
		return createCookie(name, "", 0, true, "/");
	}
}
