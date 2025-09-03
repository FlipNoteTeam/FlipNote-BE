package project.flipnote.bookmark.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.controller.docs.BookmarkControllerDocs;
import project.flipnote.bookmark.model.BookmarkTargetType;
import project.flipnote.bookmark.service.BookmarkService;
import project.flipnote.common.model.response.IdResponse;
import project.flipnote.common.security.dto.AuthPrinciple;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/bookmarks/{targetType}")
public class BookmarkController implements BookmarkControllerDocs {

	private final BookmarkService bookmarkService;

	@PostMapping("/{targetId}")
	public ResponseEntity<IdResponse> addBookmark(
		@PathVariable("targetType") BookmarkTargetType targetType,
		@PathVariable("targetId") Long targetId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		IdResponse res = bookmarkService.addBookmark(authPrinciple.userId(), targetType.toDomainType(), targetId);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	@DeleteMapping("/{targetId}")
	public ResponseEntity<IdResponse> deleteBookmark(
		@PathVariable("targetType") BookmarkTargetType targetType,
		@PathVariable("targetId") Long targetId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		IdResponse res = bookmarkService.deleteBookmark(authPrinciple.userId(), targetType.toDomainType(), targetId);

		return ResponseEntity.ok(res);
	}
}
