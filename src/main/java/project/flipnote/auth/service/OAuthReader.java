package project.flipnote.auth.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.entity.OAuthLink;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.repository.OAuthLinkRepository;
import project.flipnote.auth.repository.SocialLinkTokenRedisRepository;
import project.flipnote.common.exception.BizException;

@RequiredArgsConstructor
@Service
public class OAuthReader {

	private final SocialLinkTokenRedisRepository socialLinkTokenRedisRepository;
	private final OAuthLinkRepository oAuthLinkRepository;

	public long findAuthIdByTokenOrThrow(String token) {
		return socialLinkTokenRedisRepository.findAuthIdByToken(token)
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_SOCIAL_LINK_TOKEN));
	}

	public OAuthLink findOAuthLinkByProviderOrThrow(String providerName, String providerId) {
		return oAuthLinkRepository.findByProviderAndProviderIdWithUserAuth(providerName, providerId)
			.orElseThrow(() -> new BizException(AuthErrorCode.NOT_REGISTERED_SOCIAL_ACCOUNT));
	}
}
