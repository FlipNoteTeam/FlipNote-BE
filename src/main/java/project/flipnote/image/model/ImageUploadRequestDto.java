package project.flipnote.image.model;

import jakarta.validation.constraints.Pattern;

public record ImageUploadRequestDto(
	@Pattern(
		regexp = "^[a-fA-F0-9]{32}\\.(jpg|jpeg|png|gif)$",
		message = "파일 이름은 32자리 MD5 해시와 jpg/jpeg/png/gif 확장자 형식이어야 합니다."
	)
	String fileName
) {
}
