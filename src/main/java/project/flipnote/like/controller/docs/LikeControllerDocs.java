package project.flipnote.like.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.like.model.LikeTypeRequest;

@Tag(name = "Like", description = "Like API")
public interface LikeControllerDocs {

	@Operation(summary = "좋아요 추가", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<Void> addLike(LikeTypeRequest likeType, Long targetId, AuthPrinciple authPrinciple);
}
