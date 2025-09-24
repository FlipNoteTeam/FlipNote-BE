package project.flipnote.auth.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.config.OAuthProperties;
import project.flipnote.infra.oauth.OAuthApiClient;
import project.flipnote.infra.oauth.model.OAuth2UserInfo;

@RequiredArgsConstructor
@Service
public class OAuthUserInfoService {

	private final OAuthProviderResolver oAuthProviderResolver;
	private final OAuthApiClient oAuthApiClient;

	public OAuth2UserInfo getOAuth2UserInfo(
		String providerName,
		String code,
		String codeVerifier,
		HttpServletRequest request
	) {
		OAuthProperties.Provider provider = oAuthProviderResolver.getProvider(providerName);
		String accessToken = oAuthApiClient.requestAccessToken(provider, code, codeVerifier, request);
		Map<String, Object> userInfoAttributes = oAuthApiClient.requestUserInfo(provider, accessToken);
		return oAuthApiClient.createUserInfo(providerName, userInfoAttributes);
	}
}
