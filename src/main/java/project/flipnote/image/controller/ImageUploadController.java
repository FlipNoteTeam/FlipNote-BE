package project.flipnote.image.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.image.model.ImageUploadRequestDto;
import project.flipnote.image.model.ImageUploadResponseDto;
import project.flipnote.image.service.ImageUploadService;

@RestController("/v1/images")
@RequiredArgsConstructor
public class ImageUploadController {

	private final ImageUploadService fileService;

	//파일 업로드 API
	@PostMapping("/upload")
	public ResponseEntity<ImageUploadResponseDto> getPresignedUrl(
		@AuthenticationPrincipal AuthPrinciple authPrinciple,
		@RequestBody @Valid ImageUploadRequestDto req) {
		ImageUploadResponseDto res = fileService.getPresignedUrl(authPrinciple, req.fileName());
		return ResponseEntity.ok(res);
	}
}
