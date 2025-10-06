package project.flipnote.auth.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.repository.OAuthLinkRepository;
import project.flipnote.common.exception.BizException;

@RequiredArgsConstructor
@Service
public class OAuthPolicyService {

	private final OAuthLinkRepository oAuthLinkRepository;

	public void validateLinkNotExists(Long authId, String provider, String providerId) {
		if (oAuthLinkRepository.existsByUserAuth_IdAndProviderAndProviderId(authId, provider, providerId)) {
			throw new BizException(AuthErrorCode.ALREADY_LINKED_SOCIAL_ACCOUNT);
		}
	}
}
