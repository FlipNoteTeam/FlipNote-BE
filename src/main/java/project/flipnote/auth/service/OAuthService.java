package project.flipnote.auth.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.constants.OAuthConstants;
import project.flipnote.auth.exception.OAuthConflictException;
import project.flipnote.auth.exception.OAuthFailException;
import project.flipnote.auth.model.AuthorizationRedirect;
import project.flipnote.auth.repository.SocialLinkTokenRedisRepository;
import project.flipnote.infra.oauth.model.OAuth2UserInfo;
import project.flipnote.common.config.OAuthProperties;
import project.flipnote.common.util.CookieUtil;
import project.flipnote.common.util.PkceUtil;
import project.flipnote.infra.oauth.OAuthApiClient;
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

	public AuthorizationRedirect getAuthorizationUri(String providerName, HttpServletRequest request, long userId) {
		OAuthProperties.Provider provider = getProvider(providerName);

		String codeVerifier = pkceUtil.generateCodeVerifier();
		String codeChallenge = pkceUtil.generateCodeChallenge(codeVerifier);

		String state = UUID.randomUUID().toString();
		socialLinkTokenRedisRepository.saveToken(userId, state);

		String authorizeUrl = oAuthApiClient.buildAuthorizeUri(request, provider, codeChallenge, state);
		ResponseCookie cookie = cookieUtil.createCookie(
			OAuthConstants.VERIFIER_COOKIE_NAME,
			codeVerifier,
			OAuthConstants.VERIFIER__COOKIE_MAX_AGE
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
		long userId = socialLinkTokenRedisRepository
			.findUserIdByToken(state).orElseThrow(OAuthFailException::new);
		socialLinkTokenRedisRepository.deleteToken(state);

		OAuthProperties.Provider provider = getProvider(providerName);
		String accessToken = oAuthApiClient.requestAccessToken(provider, code, codeVerifier, request);
		Map<String, Object> userInfoAttributes = oAuthApiClient.requestUserInfo(provider, accessToken);
		OAuth2UserInfo userInfo = oAuthApiClient.createUserInfo(providerName, userInfoAttributes);

		if (userOAuthLinkRepository.existsByUser_IdAndProviderId(userId, userInfo.getProviderId())) {
			throw new OAuthConflictException();
		}

		UserOAuthLink userOAuthLink = new UserOAuthLink(
			userInfo.getProvider(),
			userInfo.getProviderId(),
			userRepository.getReferenceById(userId)
		);
		userOAuthLinkRepository.save(userOAuthLink);
	}

	private OAuthProperties.Provider getProvider(String providerName) {
		return oauthProperties.getProviders().get(providerName.toLowerCase());
	}
}
