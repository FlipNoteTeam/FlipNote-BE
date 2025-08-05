package project.flipnote.auth.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import project.flipnote.auth.model.ChangePasswordRequest;
import project.flipnote.auth.model.EmailVerifyRequest;
import project.flipnote.auth.model.EmailVerificationRequest;
import project.flipnote.auth.model.PasswordResetCreateRequest;
import project.flipnote.auth.model.PasswordResetRequest;
import project.flipnote.auth.model.UserLoginRequest;
import project.flipnote.auth.model.UserLoginResponse;
import project.flipnote.auth.model.UserRegisterRequest;
import project.flipnote.auth.model.UserRegisterResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.user.model.SocialLinksResponse;

public interface AuthControllerDocs {

	@Operation(summary = "회원가입")
	ResponseEntity<UserRegisterResponse> register(UserRegisterRequest req);

	@Operation(summary = "로그인")
	ResponseEntity<UserLoginResponse> login(UserLoginRequest req);

	@Operation(summary = "로그아웃", security = { @SecurityRequirement(name = "access-token") })
	ResponseEntity<Void> logout();

	@Operation(summary = "이메일 인증번호 전송")
	ResponseEntity<Void> sendEmailVerificationCode(EmailVerificationRequest req);

	@Operation(summary = "이메일 인증번호 확인")
	ResponseEntity<Void> verifyEmail(EmailVerifyRequest req);

	@Operation(summary = "토큰 갱신")
	ResponseEntity<UserLoginResponse> refreshToken(String refreshToken);

	@Operation(summary = "비밀번호 재설정 링크 전송")
	ResponseEntity<Void> requestPasswordReset(PasswordResetCreateRequest req);

	@Operation(summary = "비밀번호 재설정")
	ResponseEntity<Void> resetPassword(PasswordResetRequest req);

	@Operation(summary = "내 비밀번호 변경", security = { @SecurityRequirement(name = "access-token") })
	ResponseEntity<Void> updatePassword(AuthPrinciple userAuth, ChangePasswordRequest req);

	@Operation(summary = "내 소셜 연동 계정 목록 조회", security = { @SecurityRequirement(name = "access-token") })
	ResponseEntity<SocialLinksResponse> getSocialLinks(AuthPrinciple userAuth);

	@Operation(summary = "소셜 연동 해제", security = { @SecurityRequirement(name = "access-token") })
	ResponseEntity<Void> deleteSocialLink(AuthPrinciple userAuth, Long socialLinkId);
}
