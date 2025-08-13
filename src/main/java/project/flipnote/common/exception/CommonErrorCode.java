package project.flipnote.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "COMMON_001", "예기치 않은 오류가 발생했습니다."),
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "COMMON_002", "입력값이 올바르지 않습니다."),
	SERVICE_TEMPORARILY_UNAVAILABLE(HttpStatus.TOO_MANY_REQUESTS.value(), "COMMON_003", "요청이 많아 처리되지 않았습니다. 잠시 후 다시 시도해주세요.");

	private final int status;
	private final String code;
	private final String message;
}
