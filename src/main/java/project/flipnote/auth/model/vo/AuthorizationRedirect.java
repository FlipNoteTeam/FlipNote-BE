package project.flipnote.auth.model.vo;

import org.springframework.http.ResponseCookie;

public record AuthorizationRedirect(
	String authorizeUri,
	ResponseCookie cookie
) {
}
