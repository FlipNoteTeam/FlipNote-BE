package project.flipnote.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtil {

	public static Cookie createCookie(
		String name,
		String value,
		int maxAge,
		boolean httpOnly,
		String path
	) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setHttpOnly(httpOnly);
		cookie.setPath(path);
		return cookie;
	}

	public static Cookie createCookie(String name, String value, int maxAge) {
		return createCookie(name, value, maxAge, true, "/");
	}

	public static Cookie createExpiredCookie(String name) {
		return createCookie(name, "", 0, true, "/");
	}
}
