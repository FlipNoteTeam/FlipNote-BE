package project.flipnote.auth.service;

import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.constants.OAuthConstants;
import project.flipnote.auth.entity.OAuthLink;
import project.flipnote.auth.model.vo.AuthorizationRedirect;
import project.flipnote.auth.model.vo.TokenPair;
import project.flipnote.auth.repository.OAuthLinkRepository;
import project.flipnote.auth.repository.SocialLinkTokenRedisRepository;
import project.flipnote.auth.repository.UserAuthRepository;
import project.flipnote.common.config.OAuthProperties;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.common.security.jwt.JwtComponent;
import project.flipnote.common.util.CookieUtil;
import project.flipnote.common.util.PkceUtil;
import project.flipnote.infra.oauth.OAuthApiClient;
import project.flipnote.infra.oauth.model.OAuth2UserInfo;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OAuthService {

	private final PkceUtil pkceUtil;
	private final CookieUtil cookieUtil;
	private final OAuthApiClient oAuthApiClient;
	private final SocialLinkTokenRedisRepository socialLinkTokenRedisRepository;
	private final OAuthLinkRepository oAuthLinkRepository;
	private final JwtComponent jwtComponent;
	private final UserAuthRepository userAuthRepository;
	private final OAuthReader oAuthReader;
	private final OAuthProviderResolver oAuthProviderResolver;
	private final OAuthUserInfoService oAuthUserInfoService;
	private final OAuthPolicyService oAuthPolicyService;

	public AuthorizationRedirect getAuthorizationUri(
		String providerName,
		HttpServletRequest request,
		AuthPrinciple userAuth
	) {
		OAuthProperties.Provider provider = oAuthProviderResolver.getProvider(providerName);

		String codeVerifier = pkceUtil.generateCodeVerifier();
		String codeChallenge = pkceUtil.generateCodeChallenge(codeVerifier);

		String state = generateStateForSocialLink(userAuth);
		String authorizeUrl = oAuthApiClient.buildAuthorizeUri(request, provider, codeChallenge, state);
		ResponseCookie cookie = cookieUtil.createCookie(
			OAuthConstants.VERIFIER_COOKIE_NAME,
			codeVerifier,
			OAuthConstants.VERIFIER_COOKIE_MAX_AGE
		);

		return new AuthorizationRedirect(authorizeUrl, cookie);
	}

	@Transactional
	public void linkSocialAccount(
		String providerName,
		String code,
		String state,
		String codeVerifier,
		HttpServletRequest request
	) {
		long authId = oAuthReader.findAuthIdByTokenOrThrow(state);

		socialLinkTokenRedisRepository.deleteToken(state);

		OAuth2UserInfo userInfo = oAuthUserInfoService.getOAuth2UserInfo(providerName, code, codeVerifier, request);

		oAuthPolicyService.validateLinkNotExists(authId, userInfo.getProvider(), userInfo.getProviderId());

		OAuthLink userOAuthLink = new OAuthLink(
			userInfo.getProvider(),
			userInfo.getProviderId(),
			userAuthRepository.getReferenceById(authId)
		);
		oAuthLinkRepository.save(userOAuthLink);
	}

	public TokenPair socialLogin(String providerName, String code, String codeVerifier, HttpServletRequest request) {
		OAuth2UserInfo userInfo = oAuthUserInfoService.getOAuth2UserInfo(providerName, code, codeVerifier, request);

		OAuthLink userOAuthLink
			= oAuthReader.findOAuthLinkByProviderOrThrow(userInfo.getProvider(), userInfo.getProviderId());

		return jwtComponent.generateTokenPair(userOAuthLink.getUserAuth());
	}

	private String generateStateForSocialLink(AuthPrinciple userAuth) {
		if (userAuth == null) {
			return null;
		}

		String state = UUID.randomUUID().toString();
		socialLinkTokenRedisRepository.saveToken(userAuth.authId(), state);
		return state;
	}
}
