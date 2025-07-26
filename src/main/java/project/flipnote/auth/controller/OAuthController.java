package project.flipnote.auth.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.constants.OAuthConstants;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.model.AuthorizationRedirect;
import project.flipnote.auth.service.OAuthService;
import project.flipnote.common.config.ClientProperties;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OAuthController {

	private final OAuthService oAuthService;
	private final ClientProperties clientProperties;

	@GetMapping("/oauth2/authorization/{provider}")
	public ResponseEntity<Void> redirectToProviderAuthorization(
		@PathVariable("provider") String provider,
		HttpServletRequest request,
		@AuthenticationPrincipal UserAuth userAuth
	) {
		AuthorizationRedirect authRedirect = oAuthService.getAuthorizationUri(provider, request, 1L);

		return ResponseEntity.status(HttpStatus.FOUND)
			.header(HttpHeaders.SET_COOKIE, authRedirect.cookie().toString())
			.location(URI.create(authRedirect.authorizeUri()))
			.build();
	}

	@GetMapping("/oauth2/callback/{provider}")
	public ResponseEntity<Void> handleCallback(
		@PathVariable("provider") String provider,
		@RequestParam("code") String code,
		@RequestParam("state") String state,
		@CookieValue(OAuthConstants.VERIFIER_COOKIE_NAME) String codeVerifier,
		HttpServletRequest request
	) {
		String redirectUri = clientProperties.buildUrl(ClientProperties.PathKey.SOCIAL_LINK_SUCCESS);
		try {
			oAuthService.linkSocialAccount(provider, code, state, codeVerifier, request);
		} catch (BizException exception) {
			if (exception.getErrorCode() == AuthErrorCode.ALREADY_LINKED_SOCIAL_ACCOUNT) {
				redirectUri = clientProperties.buildUrl(ClientProperties.PathKey.SOCIAL_LINK_CONFLICT);
			} else {
				redirectUri = clientProperties.buildUrl(ClientProperties.PathKey.SOCIAL_LINK_FAILURE);
			}
			log.warn("BizException handled: code={}, status={}, message={}",
				exception.getErrorCode().getCode(),
				exception.getErrorCode().getStatus(),
				exception.getErrorCode().getMessage()
			);
		} catch (Exception exception) {
			redirectUri = clientProperties.buildUrl(ClientProperties.PathKey.SOCIAL_LINK_FAILURE);
			log.error("소셜 계정 연동 콜백 처리 중 예상치 못한 오류 발생. provider: {}, state: {}", provider, state, exception);
		}

		return ResponseEntity.status(HttpStatus.FOUND)
			.location(URI.create(redirectUri))
			.build();
	}
}
