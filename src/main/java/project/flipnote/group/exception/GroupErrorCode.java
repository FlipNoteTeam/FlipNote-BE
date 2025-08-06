package project.flipnote.group.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {
	GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_002", "그룹이 존재하지 않습니다."),
	INVALID_MAX_MEMBER(HttpStatus.BAD_REQUEST, "GROUP_001", "최대 인원 수는 1 이상 100 이하여야 합니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
