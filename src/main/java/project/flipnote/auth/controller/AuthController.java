package project.flipnote.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.auth.model.EmailVerificationConfirmRequest;
import project.flipnote.auth.model.EmailVerificationRequest;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.model.UserLoginRequest;
import project.flipnote.auth.model.UserLoginResponse;
import project.flipnote.auth.service.AuthService;
import project.flipnote.common.security.jwt.JwtConstants;
import project.flipnote.common.security.jwt.JwtProperties;
import project.flipnote.common.util.CookieUtil;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

	private final AuthService authService;
	private final JwtProperties jwtProperties;

	@PostMapping("/login")
	public ResponseEntity<UserLoginResponse> login(
		@Valid @RequestBody UserLoginRequest req
	) {
		TokenPair tokenPair = authService.login(req);

		long expirationSeconds = jwtProperties.getRefreshTokenExpiration().toSeconds();
		Cookie cookie = CookieUtil.createCookie(
			JwtConstants.REFRESH_TOKEN,
			tokenPair.refreshToken(),
			Math.toIntExact(expirationSeconds)
		);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.body(UserLoginResponse.from(tokenPair.accessToken()));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		Cookie expiredCookie = CookieUtil.createExpiredCookie(JwtConstants.REFRESH_TOKEN);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
			.build();
	}

	@PostMapping("/email")
	public ResponseEntity<Void> sendEmailVerificationCode(@Valid @RequestBody EmailVerificationRequest req) {
		authService.sendEmailVerificationCode(req);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/email/confirm")
	public ResponseEntity<Void> confirmEmailVerificationCode(
		@Valid @RequestBody EmailVerificationConfirmRequest req
	) {
		authService.confirmEmailVerificationCode(req);

		return ResponseEntity.ok().build();
	}
}
