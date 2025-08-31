package project.flipnote.image.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import project.flipnote.image.model.ImageUploadRequestDto;
import project.flipnote.image.model.ImageUploadResponseDto;

public interface ImageUploadControllerDocs {
	//이미지 업로드 url 생성
	@Operation(summary = "이미지 업로드 url 생성")
	public ResponseEntity<ImageUploadResponseDto> getPresignedUrl(ImageUploadRequestDto req);
}
