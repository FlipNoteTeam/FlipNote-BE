package project.flipnote.cardset.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum CardSetErrorCode implements ErrorCode {

	GROUP_MEMBER_NOT_FOUND(HttpStatus.FORBIDDEN, "CARDSET_001", "해당 그룹의 멤버가 아닙니다."),
	CARD_SET_NOT_FOUND(HttpStatus.NOT_FOUND, "CARDSET_002", "카드셋이 존재하지 않습니다."),
	CARD_SET_PRIVATE(HttpStatus.FORBIDDEN, "CARDSET_003", "비공개 카드셋입니다."),
	CARD_SET_NO_EDIT_PERMISSION(HttpStatus.FORBIDDEN, "CARDSET_004", "카드셋 수정 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
