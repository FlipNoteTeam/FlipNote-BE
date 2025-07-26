package project.flipnote.auth.intecepter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import project.flipnote.auth.constants.OAuthConstants;
import project.flipnote.common.util.CookieUtil;

@RequiredArgsConstructor
@Component
public class OAuthCookieCleanupInterceptor implements HandlerInterceptor {

	private final CookieUtil cookieUtil;

	@Override
	public void afterCompletion(
		HttpServletRequest request,
		HttpServletResponse response,
		Object handler,
		Exception ex
	) {
		ResponseCookie expiredCookie = cookieUtil.createExpiredCookie(OAuthConstants.VERIFIER_COOKIE_NAME);
		response.addHeader(HttpHeaders.SET_COOKIE, expiredCookie.toString());
	}
}
