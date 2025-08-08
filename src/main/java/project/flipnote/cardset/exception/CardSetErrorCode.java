package project.flipnote.cardset.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CardSetErrorCode implements ErrorCode {

	GROUP_MEMBER_NOT_FOUND(HttpStatus.FORBIDDEN, "CARDSET_001", "해당 그룹의 멤버가 아닙니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
