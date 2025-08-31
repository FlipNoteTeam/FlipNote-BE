package project.flipnote.image.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.image.model.ImageUploadRequestDto;
import project.flipnote.image.model.ImageUploadResponseDto;

@Tag(name = "이미지 업로드", description = "S3 Presigned URL 관리 API")
public interface ImageUploadControllerDocs {
	//이미지 업로드 url 생성
	@Operation(
		summary = "이미지 업로드 URL 생성",
		description = "S3에 이미지를 업로드할 수 있는 Presigned PUT URL을 발급합니다. "
			+ "파일 이름은 32자리 MD5 + 확장자(jpg|jpeg|png|gif) 형식이어야 합니다."
	)
	@RequestBody(
		description = "이미지 업로드 요청(파일명)",
		required = true,
		content = @Content(schema = @Schema(implementation = ImageUploadRequestDto.class))
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "URL 발급 성공",
			content = @Content(schema = @Schema(implementation = ImageUploadResponseDto.class))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청(파일명 형식 오류 등)"),
		@ApiResponse(responseCode = "409", description = "이미 존재하는 파일명(CONFLICT_IMAGE)"),
		@ApiResponse(responseCode = "500", description = "S3 서비스 오류(S3_SERVICE_ERROR)")
	})
	public ResponseEntity<ImageUploadResponseDto> getPresignedUrl(ImageUploadRequestDto req);
}
