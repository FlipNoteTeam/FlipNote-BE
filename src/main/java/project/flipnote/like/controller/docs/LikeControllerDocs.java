package project.flipnote.like.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.like.model.response.LikeResponse;
import project.flipnote.like.model.request.LikeSearchRequest;
import project.flipnote.like.model.response.LikeTargetResponse;
import project.flipnote.like.model.request.LikeTargetTypeRequest;

@Tag(name = "Like", description = "Like API")
public interface LikeControllerDocs {

	@Operation(summary = "좋아요 추가", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<Void> addLike(LikeTargetTypeRequest targetType, Long targetId, AuthPrinciple authPrinciple);

	@Operation(summary = "좋아요 취소", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<Void> removeLike(LikeTargetTypeRequest targetType, Long targetId, AuthPrinciple authPrinciple);

	@Operation(summary = "좋아요 누른 목록 조회", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<PagingResponse<LikeResponse<LikeTargetResponse>>> getLikes(
		LikeTargetTypeRequest targetType,
		LikeSearchRequest req,
		AuthPrinciple authPrinciple
	);
}
