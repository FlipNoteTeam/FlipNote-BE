package project.flipnote.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.auth.model.EmailVerificationDto;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.model.UserLoginDto;
import project.flipnote.auth.service.AuthService;
import project.flipnote.common.security.jwt.JwtConstants;
import project.flipnote.common.util.CookieUtil;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<UserLoginDto.Response> login(
		@Valid @RequestBody UserLoginDto.Request req,
		HttpServletResponse servletResponse
	) {
		TokenPair tokenPair = authService.login(req);

		CookieUtil.addCookie(servletResponse, JwtConstants.REFRESH_TOKEN, tokenPair.refreshToken(), 30 * 24 * 60 * 60);

		return ResponseEntity.ok(UserLoginDto.Response.from(tokenPair.accessToken()));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse servletResponse) {
		CookieUtil.deleteCookie(servletResponse, JwtConstants.REFRESH_TOKEN);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/email")
	public ResponseEntity<Void> sendEmailVerificationCode(@Valid @RequestBody EmailVerificationDto.Request req) {
		authService.sendEmailVerificationCode(req);

		return ResponseEntity.ok().build();
	}
}
