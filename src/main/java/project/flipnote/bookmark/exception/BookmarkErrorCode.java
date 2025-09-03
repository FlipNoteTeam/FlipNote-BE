package project.flipnote.bookmark.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum BookmarkErrorCode implements ErrorCode {
	BOOKMARK_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK_001", "즐겨찾기 대상이 존재하지 않습니다."),
	BOOKMARK_ALREADY_EXISTS(HttpStatus.CONFLICT, "BOOKMARK_002", "이미 즐겨찾기 되어 있습니다."),
	BOOKMARK_FETCHER_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, "BOOKMARK_003", "현재 즐겨찾기 할 수 없는 대상입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
