package project.flipnote.common.security.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements ErrorCode {
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "SECURITY_0021", "토큰이 만료되었습니다."),
	NOT_VALID_JWT_TOKEN(HttpStatus.UNAUTHORIZED.value(), "SECURITY_002", "올바르지 않은 토큰입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "SECURITY_003", "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN.value(), "SECURITY_004", "권한이 없습니다.");

	private final int status;
	private final String code;
	private final String message;
}
