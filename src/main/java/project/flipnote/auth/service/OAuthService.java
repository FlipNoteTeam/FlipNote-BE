package project.flipnote.auth.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.constants.OAuthConstants;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.model.AuthorizationRedirect;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.repository.SocialLinkTokenRedisRepository;
import project.flipnote.common.config.OAuthProperties;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.common.security.jwt.JwtComponent;
import project.flipnote.common.util.CookieUtil;
import project.flipnote.common.util.PkceUtil;
import project.flipnote.infra.oauth.OAuthApiClient;
import project.flipnote.infra.oauth.model.OAuth2UserInfo;
import project.flipnote.user.entity.UserOAuthLink;
import project.flipnote.user.repository.UserOAuthLinkRepository;
import project.flipnote.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OAuthService {

	private final OAuthProperties oauthProperties;
	private final PkceUtil pkceUtil;
	private final CookieUtil cookieUtil;
	private final OAuthApiClient oAuthApiClient;
	private final SocialLinkTokenRedisRepository socialLinkTokenRedisRepository;
	private final UserRepository userRepository;
	private final UserOAuthLinkRepository userOAuthLinkRepository;
	private final JwtComponent jwtComponent;

	public AuthorizationRedirect getAuthorizationUri(
		String providerName,
		HttpServletRequest request,
		UserAuth userAuth
	) {
		OAuthProperties.Provider provider = getProvider(providerName);

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
		long userId = socialLinkTokenRedisRepository.findUserIdByToken(state)
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_SOCIAL_LINK_TOKEN));
		socialLinkTokenRedisRepository.deleteToken(state);

		OAuth2UserInfo userInfo = getOAuth2UserInfo(providerName, code, codeVerifier, request);

		if (userOAuthLinkRepository.existsByUser_IdAndProviderId(userId, userInfo.getProviderId())) {
			throw new BizException(AuthErrorCode.ALREADY_LINKED_SOCIAL_ACCOUNT);
		}

		UserOAuthLink userOAuthLink = new UserOAuthLink(
			userInfo.getProvider(),
			userInfo.getProviderId(),
			userRepository.getReferenceById(userId)
		);
		userOAuthLinkRepository.save(userOAuthLink);
	}

	public TokenPair socialLogin(String providerName, String code, String codeVerifier, HttpServletRequest request) {
		OAuth2UserInfo userInfo = getOAuth2UserInfo(providerName, code, codeVerifier, request);

		UserOAuthLink userOAuthLink = userOAuthLinkRepository.findByProviderAndProviderIdWithUser(
			providerName, userInfo.getProviderId()
		).orElseThrow(() -> new BizException(AuthErrorCode.NOT_REGISTERED_SOCIAL_ACCOUNT));

		return jwtComponent.generateTokenPair(userOAuthLink.getUser());
	}

	private OAuth2UserInfo getOAuth2UserInfo(String providerName, String code, String codeVerifier,
		HttpServletRequest request) {
		OAuthProperties.Provider provider = getProvider(providerName);
		String accessToken = oAuthApiClient.requestAccessToken(provider, code, codeVerifier, request);
		Map<String, Object> userInfoAttributes = oAuthApiClient.requestUserInfo(provider, accessToken);
		return oAuthApiClient.createUserInfo(providerName, userInfoAttributes);
	}

	private OAuthProperties.Provider getProvider(String providerName) {
		return Optional.ofNullable(oauthProperties.getProviders().get(providerName.toLowerCase()))
			.orElseThrow(() -> {
				log.warn("지원하지 않는 OAuth Provider 입니다. provider: {}", providerName);
				return new BizException(AuthErrorCode.INVALID_OAUTH_PROVIDER);
			});
	}

	private String generateStateForSocialLink(UserAuth userAuth) {
		if (userAuth == null) {
			return null;
		}
		String state = UUID.randomUUID().toString();
		socialLinkTokenRedisRepository.saveToken(userAuth.userId(), state);
		return state;
	}
}
