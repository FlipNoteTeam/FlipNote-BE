package project.flipnote.like.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum LikeErrorCode implements ErrorCode {

	INVALID_LIKE_TYPE(HttpStatus.BAD_REQUEST, "LIKE_001", "유효하지 않은 좋아요 타입입니다."),
	LIKE_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE_002", "좋아요 대상이 존재하지 않습니다."),
	ALREADY_LIKED(HttpStatus.CONFLICT, "LIKE_003", "이미 좋아요를 눌렀습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
