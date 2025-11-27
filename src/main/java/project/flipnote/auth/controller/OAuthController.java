package project.flipnote.auth.controller;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.constants.OAuthConstants;
import project.flipnote.auth.controller.docs.OAuthControllerDocs;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.model.vo.AuthorizationRedirect;
import project.flipnote.auth.model.vo.TokenPair;
import project.flipnote.auth.service.OAuthService;
import project.flipnote.common.config.ClientProperties;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.common.security.jwt.JwtConstants;
import project.flipnote.common.security.jwt.JwtProperties;
import project.flipnote.common.util.CookieUtil;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OAuthController implements OAuthControllerDocs {

	private final OAuthService oAuthService;
	private final ClientProperties clientProperties;
	private final JwtProperties jwtProperties;
	private final CookieUtil cookieUtil;

	@GetMapping("/oauth2/authorization/{provider}")
	public ResponseEntity<String> redirectToProviderAuthorization(
		@PathVariable("provider") String provider,
		HttpServletRequest request,
		@AuthenticationPrincipal AuthPrinciple userAuth
	) {
		AuthorizationRedirect authRedirect = oAuthService.getAuthorizationUri(provider, request, userAuth);

		return ResponseEntity.status(HttpStatus.FOUND)
			.header(HttpHeaders.SET_COOKIE, authRedirect.cookie().toString())
			.location(URI.create(authRedirect.authorizeUri()))
			.build();
	}

	@GetMapping("/oauth2/callback/{provider}")
	public ResponseEntity<Void> handleCallback(
		@PathVariable("provider") String provider,
		@RequestParam("code") String code,
		@RequestParam(name = "state", required = false) String state,
		@CookieValue(OAuthConstants.VERIFIER_COOKIE_NAME) String codeVerifier,
		HttpServletRequest request
	) {
		boolean isSocialLinkRequest = StringUtils.hasText(state);
		if (isSocialLinkRequest) {
			return handleSocialLink(provider, code, state, codeVerifier, request);
		}
		return handleSocialLogin(provider, code, codeVerifier, request);
	}

	private ResponseEntity<Void> handleSocialLink(
		String provider,
		String code,
		String state,
		String codeVerifier,
		HttpServletRequest request
	) {
		URI location;
		try {
			oAuthService.linkSocialAccount(provider, code, state, codeVerifier, request);
			location = buildRedirectUri(ClientProperties.PathKey.SOCIAL_LINK_SUCCESS);
		} catch (BizException ex) {
			location = resolveRedirectUrlForSocialLink(ex);
			logBizException(ex);
		} catch (Exception ex) {
			location = buildRedirectUri(ClientProperties.PathKey.SOCIAL_LINK_FAILURE);
			log.error("소셜 계정 연동 콜백 처리 중 예상치 못한 오류 발생. provider: {}, state: {}", provider, state, ex);
		}

		return buildRedirectResponse(location, null);
	}

	private ResponseEntity<Void> handleSocialLogin(
		String provider,
		String code,
		String codeVerifier,
		HttpServletRequest request
	) {
		URI location;
		ResponseCookie refreshTokenCookie = null;
		try {
			TokenPair tokenPair = oAuthService.socialLogin(provider, code, codeVerifier, request);
			location = buildLoginSuccessRedirectUri(tokenPair.accessToken());
			refreshTokenCookie = createRefreshTokenCookie(tokenPair.refreshToken());
		} catch (BizException ex) {
			location = buildRedirectUri(ClientProperties.PathKey.SOCIAL_LOGIN_FAILURE);
			logBizException(ex);
		} catch (Exception ex) {
			location = buildRedirectUri(ClientProperties.PathKey.SOCIAL_LOGIN_FAILURE);
			log.error("소셜 계정 로그인 콜백 처리 중 예상치 못한 오류 발생. provider: {}", provider, ex);
		}

		return buildRedirectResponse(location, refreshTokenCookie);
	}

	private void logBizException(BizException ex) {
		log.warn("BizException handled: code={}, status={}, message={}",
			ex.getErrorCode().getCode(),
			ex.getErrorCode().getStatus(),
			ex.getErrorCode().getMessage()
		);
	}

	private ResponseCookie createRefreshTokenCookie(String token) {
		long expirationSeconds = jwtProperties.getRefreshTokenExpiration().toSeconds();
		return cookieUtil.createCookie(
			JwtConstants.REFRESH_TOKEN,
			token,
			Math.toIntExact(expirationSeconds)
		);
	}

	private URI resolveRedirectUrlForSocialLink(BizException exception) {
		if (exception.getErrorCode() == AuthErrorCode.ALREADY_LINKED_SOCIAL_ACCOUNT) {
			return buildRedirectUri(ClientProperties.PathKey.SOCIAL_LINK_CONFLICT);
		}
		return buildRedirectUri(ClientProperties.PathKey.SOCIAL_LINK_FAILURE);
	}

	private URI buildRedirectUri(ClientProperties.PathKey pathKey) {
		return URI.create(clientProperties.buildUrl(pathKey));
	}

	private URI buildLoginSuccessRedirectUri(String accessToken) {
		return UriComponentsBuilder
			.fromUriString(clientProperties.buildUrl(ClientProperties.PathKey.SOCIAL_LOGIN_SUCCESS))
			.queryParam("accessToken", accessToken)
			.build(true)
			.toUri();
	}

	private ResponseEntity<Void> buildRedirectResponse(URI location, ResponseCookie cookie) {
		ResponseEntity.BodyBuilder builder = ResponseEntity.status(HttpStatus.FOUND).location(location);
		if (cookie != null) {
			builder.header(HttpHeaders.SET_COOKIE, cookie.toString());
		}
		return builder.build();
	}
}
