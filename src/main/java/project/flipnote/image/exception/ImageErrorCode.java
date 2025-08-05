package project.flipnote.image.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
	CONFLICT_IMAGE(HttpStatus.CONFLICT, "IMAGE_001", "이미 존재하는 파일입니다."),
	S3_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_002", "S3 서비스 처리 중 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
