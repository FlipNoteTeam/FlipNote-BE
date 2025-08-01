package project.flipnote.auth.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import project.flipnote.common.security.dto.AuthPrinciple;

@Tag(name = "OAuth", description = "OAuth API")
public interface OAuthControllerDocs {

	@Operation(summary = "소셜 인증 URL로 리다이렉트")
	ResponseEntity<Void> redirectToProviderAuthorization(
		String provider,
		HttpServletRequest request,
		AuthPrinciple userAuth
	);

	@Operation(summary = "소셜 계정 연동 및 로그인")
	ResponseEntity<Void> handleCallback(
		String provider,
		String code,
		String state,
		String codeVerifier,
		HttpServletRequest request
	);
}
