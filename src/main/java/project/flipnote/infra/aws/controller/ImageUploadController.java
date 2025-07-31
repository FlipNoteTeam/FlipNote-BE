package project.flipnote.infra.aws.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.infra.aws.model.ImageUploadRequestDto;
import project.flipnote.infra.aws.model.ImageUploadResponseDto;
import project.flipnote.infra.aws.service.ImageUploadService;

@RestController
@RequiredArgsConstructor
public class ImageUploadController {

	private final ImageUploadService fileService;

	@PostMapping("/fileName")
	public ResponseEntity<ImageUploadResponseDto> getPresignedUrl(
		UserAuth userAuth,
		@RequestBody ImageUploadRequestDto req) {
		ImageUploadResponseDto res = fileService.getPresignedUrl(userAuth, req.fileName());
		return ResponseEntity.ok(res);
	}
}
