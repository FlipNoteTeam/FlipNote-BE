package project.flipnote.auth.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "이메일 또는 비밀번호가 올바르지 않습니다."),
	ALREADY_ISSUED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "AUTH_002", "이미 발급된 인증번호가 있습니다. 잠시 후 다시 시도해 주세요."),
	NOT_ISSUED_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "AUTH_003", "발급된 인증번호가 없습니다. 인증번호를 먼저 요청해 주세요."),
	INVALID_VERIFICATION_CODE(HttpStatus.FORBIDDEN, "AUTH_004", "잘못된 인증번호입니다. 입력한 인증번호를 확인해 주세요."),
	EXISTING_EMAIL(HttpStatus.CONFLICT, "AUTH_005", "이미 가입된 이메일입니다. 다른 이메일을 사용해 주세요."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_006", "인증 정보가 유효하지 않습니다."),
	UNVERIFIED_EMAIL(HttpStatus.FORBIDDEN, "AUTH_007", "인증되지 않은 이메일입니다. 이메일 인증을 완료해 주세요."),
	ALREADY_SENT_PASSWORD_RESET_LINK(HttpStatus.CONFLICT, "AUTH_008", "이미 유효한 비밀번호 재설정 링크가 존재합니다. 이메일을 확인해주세요."),
	INVALID_PASSWORD_RESET_TOKEN(HttpStatus.NOT_FOUND, "AUTH_009", "비밀번호 재설정 링크가 유효하지 않거나 만료되었습니다."),
	INVALID_SOCIAL_LINK_TOKEN(HttpStatus.NOT_FOUND, "AUTH_010", "소셜 계정 연동 토큰이 유효하지 않거나 만료되었습니다."),
	ALREADY_LINKED_SOCIAL_ACCOUNT(HttpStatus.NOT_FOUND, "AUTH_011", "이미 연동된 소셜 계정입니다."),
	INVALID_PASSWORD_RESET_TOKEN(HttpStatus.NOT_FOUND, "AUTH_009", "비밀번호 재설정 링크가 유효하지 않거나 만료되었습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
