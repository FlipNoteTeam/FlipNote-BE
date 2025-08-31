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
import project.flipnote.like.model.LikeResponse;
import project.flipnote.like.model.LikeSearchRequest;
import project.flipnote.like.model.LikeTargetResponse;
import project.flipnote.like.model.LikeTypeRequest;
import project.flipnote.like.service.LikeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/likes")
public class LikeController implements LikeControllerDocs {

	private final LikeService likeService;

	@PostMapping("/{type}/{targetId}")
	public ResponseEntity<Void> addLike(
		@PathVariable("type") LikeTypeRequest likeType,
		@PathVariable("targetId") Long targetId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		likeService.addLike(authPrinciple.userId(), likeType.toDomain(), targetId);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{type}/{targetId}")
	public ResponseEntity<Void> removeLike(
		@PathVariable("type") LikeTypeRequest likeType,
		@PathVariable("targetId") Long targetId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		likeService.removeLike(authPrinciple.userId(), likeType.toDomain(), targetId);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{type}")
	public ResponseEntity<PagingResponse<LikeResponse<LikeTargetResponse>>> getLikes(
		@PathVariable(name = "type") LikeTypeRequest likeType,
		@Valid @ModelAttribute LikeSearchRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		PagingResponse<LikeResponse<LikeTargetResponse>> res
			= likeService.getLikes(authPrinciple.userId(), likeType.toDomain(), req);

		return ResponseEntity.ok(res);
	}
}
