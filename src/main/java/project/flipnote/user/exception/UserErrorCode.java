package project.flipnote.user.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

	DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_001", "이미 사용 중인 이메일입니다."),
	DUPLICATE_PHONE(HttpStatus.CONFLICT, "USER_002", "이미 사용 중인 휴대전화 번호입니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_003", "회원이 존재하지 않습니다."),
	UNVERIFIED_EMAIL(HttpStatus.FORBIDDEN, "USER_004", "인증되지 않은 이메일입니다. 이메일 인증을 완료해 주세요.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
