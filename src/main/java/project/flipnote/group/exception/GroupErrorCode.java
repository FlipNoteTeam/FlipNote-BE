package project.flipnote.group.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
