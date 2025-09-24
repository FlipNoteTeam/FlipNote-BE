package project.flipnote.auth.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.common.config.OAuthProperties;
import project.flipnote.common.exception.BizException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuthProviderResolver {

	private final OAuthProperties oAuthProperties;

	public OAuthProperties.Provider getProvider(String providerName) {
		if (StringUtils.isEmpty(providerName)) {
			throw new BizException(AuthErrorCode.INVALID_OAUTH_PROVIDER);
		}

		Map<String, OAuthProperties.Provider> providers = oAuthProperties.getProviders();
		if (providers == null) {
			throw new BizException(AuthErrorCode.INVALID_OAUTH_PROVIDER);
		}

		OAuthProperties.Provider provider = providers.get(providerName.toLowerCase());
		if (provider == null) {
			log.warn("지원하지 않는 OAuth Provider 입니다. provider: {}", providerName);
			throw new BizException(AuthErrorCode.INVALID_OAUTH_PROVIDER);
		}

		return provider;
	}
}
