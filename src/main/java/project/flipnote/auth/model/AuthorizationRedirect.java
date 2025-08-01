package project.flipnote.auth.model;

import org.springframework.http.ResponseCookie;

public record AuthorizationRedirect(
	String authorizeUri,
	ResponseCookie cookie
) {
}
