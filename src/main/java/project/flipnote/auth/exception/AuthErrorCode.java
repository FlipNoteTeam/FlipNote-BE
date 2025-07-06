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
	UNVERIFIED_EMAIL(HttpStatus.FORBIDDEN, "AUTH_005", "인증되지 않은 이메일입니다. 이메일 인증을 완료해 주세요.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
