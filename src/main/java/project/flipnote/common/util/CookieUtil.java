package project.flipnote.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieUtil {

	public static void addCookie(
		HttpServletResponse response,
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
		response.addCookie(cookie);
	}

	public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
		addCookie(response, name, value, maxAge, true, "/");
	}

	public static void deleteCookie(HttpServletResponse response, String name) {
		addCookie(response, name, "", 0, true, "/");
	}
}
