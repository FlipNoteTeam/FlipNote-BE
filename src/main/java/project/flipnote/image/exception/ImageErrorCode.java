package project.flipnote.image.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements ErrorCode {
	CONFLICT_IMAGE(HttpStatus.CONFLICT, "IMAGE_001", "이미 존재하는 파일입니다."),
	S3_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_002", "S3 서비스 처리 중 오류가 발생했습니다."),
	IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND,"IMAGE_003", "이미지가 존재하지 않습니다."),
	INVALID_URL(HttpStatus.BAD_REQUEST, "IMAGE_004", "URL이 적절하지 않습니다."),
	CONFLICT_IMAGE_REF(HttpStatus.CONFLICT, "IMAGE_005", "이미 존재하는 이미지 참조입니다. 다시 업로드 해주세요.");
	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
