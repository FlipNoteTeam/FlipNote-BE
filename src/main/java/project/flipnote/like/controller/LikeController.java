package project.flipnote.like.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.like.controller.docs.LikeControllerDocs;
import project.flipnote.like.model.response.LikeResponse;
import project.flipnote.like.model.request.LikeSearchRequest;
import project.flipnote.like.model.response.LikeTargetResponse;
import project.flipnote.like.model.request.LikeTargetTypeRequest;
import project.flipnote.like.service.LikeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/likes")
public class LikeController implements LikeControllerDocs {

	private final LikeService likeService;

	@PostMapping("/{targetType}/{targetId}")
	public ResponseEntity<Void> addLike(
		@PathVariable("targetType") LikeTargetTypeRequest targetType,
		@PathVariable("targetId") Long targetId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		likeService.addLike(authPrinciple.userId(), targetType.toDomainType(), targetId);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{targetType}/{targetId}")
	public ResponseEntity<Void> removeLike(
		@PathVariable("targetType") LikeTargetTypeRequest targetType,
		@PathVariable("targetId") Long targetId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		likeService.removeLike(authPrinciple.userId(), targetType.toDomainType(), targetId);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{targetType}")
	public ResponseEntity<PagingResponse<LikeResponse<LikeTargetResponse>>> getLikes(
		@PathVariable(name = "targetType") LikeTargetTypeRequest targetType,
		@Valid @ModelAttribute LikeSearchRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		PagingResponse<LikeResponse<LikeTargetResponse>> res
			= likeService.getLikes(authPrinciple.userId(), targetType.toDomainType(), req);

		return ResponseEntity.ok(res);
	}
}
