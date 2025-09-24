package project.flipnote.auth.service;

import java.util.Optional;

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
		return Optional.ofNullable(oAuthProperties.getProviders().get(providerName.toLowerCase()))
			.orElseThrow(() -> {
				log.warn("지원하지 않는 OAuth Provider 입니다. provider: {}", providerName);
				return new BizException(AuthErrorCode.INVALID_OAUTH_PROVIDER);
			});
	}
}
