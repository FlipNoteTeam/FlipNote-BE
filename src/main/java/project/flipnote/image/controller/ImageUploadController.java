package project.flipnote.image.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.image.controller.docs.ImageUploadControllerDocs;
import project.flipnote.image.model.ImageUploadRequestDto;
import project.flipnote.image.model.ImageUploadResponseDto;
import project.flipnote.image.service.ImageUploadService;

@RestController
@RequestMapping("/v1/images")
@RequiredArgsConstructor
public class ImageUploadController implements ImageUploadControllerDocs {
	private final ImageUploadService fileService;

	//파일 업로드 API
	@PostMapping("/upload")
	public ResponseEntity<ImageUploadResponseDto> getPresignedUrl(
		@RequestBody @Valid ImageUploadRequestDto req) {
		ImageUploadResponseDto res = fileService.getPresignedUrl(req.fileName());
		return ResponseEntity.ok(res);
	}
}
