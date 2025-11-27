package project.flipnote.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.auth.controller.docs.AuthControllerDocs;
import project.flipnote.auth.model.request.ChangePasswordRequest;
import project.flipnote.auth.model.request.EmailVerificationRequest;
import project.flipnote.auth.model.request.EmailVerifyRequest;
import project.flipnote.auth.model.request.PasswordResetCreateRequest;
import project.flipnote.auth.model.request.PasswordResetRequest;
import project.flipnote.auth.model.request.UserLoginRequest;
import project.flipnote.auth.model.request.UserRegisterRequest;
import project.flipnote.auth.model.response.UserRegisterResponse;
import project.flipnote.auth.model.vo.TokenPair;
import project.flipnote.auth.service.AuthService;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.common.security.jwt.JwtConstants;
import project.flipnote.common.security.jwt.JwtProperties;
import project.flipnote.common.util.CookieUtil;
import project.flipnote.user.model.SocialLinksResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/auth")
public class AuthController implements AuthControllerDocs {

	private final AuthService authService;
	private final JwtProperties jwtProperties;
	private final CookieUtil cookieUtil;

	@PostMapping("/register")
	public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest req) {
		UserRegisterResponse res = authService.register(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	@PostMapping("/login")
	public ResponseEntity<Void> login(
		@Valid @RequestBody UserLoginRequest req
	) {
		TokenPair tokenPair = authService.login(req);

		long accessTokenExpire = jwtProperties.getAccessTokenExpiration().toSeconds();
		ResponseCookie accessCookie = cookieUtil.createCookie(
			JwtConstants.ACCESS_TOKEN,
			tokenPair.accessToken(),
			Math.toIntExact(accessTokenExpire)
		);

		long expirationSeconds = jwtProperties.getRefreshTokenExpiration().toSeconds();
		ResponseCookie cookie = cookieUtil.createCookie(
			JwtConstants.REFRESH_TOKEN,
			tokenPair.refreshToken(),
			Math.toIntExact(expirationSeconds)
		);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, accessCookie.toString())
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.build();
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout() {
		ResponseCookie expiredCookie = cookieUtil.createExpiredCookie(JwtConstants.REFRESH_TOKEN);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
			.build();
	}

	@PostMapping("/email-verification/request")
	public ResponseEntity<Void> sendEmailVerificationCode(@Valid @RequestBody EmailVerificationRequest req) {
		authService.sendEmailVerificationCode(req);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/email-verification")
	public ResponseEntity<Void> verifyEmail(
		@Valid @RequestBody EmailVerifyRequest req
	) {
		authService.verifyEmail(req);

		return ResponseEntity.ok().build();
	}

	@PostMapping("/token/refresh")
	public ResponseEntity<Void> refreshToken(
		@CookieValue(name = JwtConstants.REFRESH_TOKEN) String refreshToken
	) {
		TokenPair tokenPair = authService.refreshToken(refreshToken);

		long accessTokenExpire = jwtProperties.getAccessTokenExpiration().toSeconds();
		ResponseCookie accessCookie = cookieUtil.createCookie(
			JwtConstants.ACCESS_TOKEN,
			tokenPair.accessToken(),
			Math.toIntExact(accessTokenExpire)
		);

		long expirationSeconds = jwtProperties.getRefreshTokenExpiration().toSeconds();
		ResponseCookie cookie = cookieUtil.createCookie(
			JwtConstants.REFRESH_TOKEN,
			tokenPair.refreshToken(),
			Math.toIntExact(expirationSeconds)
		);

		return ResponseEntity.ok()
			.header(HttpHeaders.SET_COOKIE, accessCookie.toString())
			.header(HttpHeaders.SET_COOKIE, cookie.toString())
			.build();
	}

	@PostMapping("/password-reset/request")
	public ResponseEntity<Void> requestPasswordReset(
		@Valid @RequestBody PasswordResetCreateRequest req
	) {
		authService.requestPasswordReset(req);

		return ResponseEntity.noContent().build();
	}

	@PostMapping("/password-reset")
	public ResponseEntity<Void> resetPassword(
		@Valid @RequestBody PasswordResetRequest req
	) {
		authService.resetPassword(req);

		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/password")
	public ResponseEntity<Void> updatePassword(
		@AuthenticationPrincipal AuthPrinciple userAuth,
		@Valid @RequestBody ChangePasswordRequest req
	) {
		authService.changePassword(userAuth.authId(), req);

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/social-links")
	public ResponseEntity<SocialLinksResponse> getSocialLinks(
		@AuthenticationPrincipal AuthPrinciple userAuth
	) {
		SocialLinksResponse res = authService.getSocialLinks(userAuth.authId());

		return ResponseEntity.ok(res);
	}

	@DeleteMapping("/social-links/{socialLinkId}")
	public ResponseEntity<Void> deleteSocialLink(
		@AuthenticationPrincipal AuthPrinciple userAuth,
		@PathVariable("socialLinkId") Long socialLinkId
	) {
		authService.deleteSocialLink(userAuth.authId(), socialLinkId);

		return ResponseEntity.noContent().build();
	}
}
