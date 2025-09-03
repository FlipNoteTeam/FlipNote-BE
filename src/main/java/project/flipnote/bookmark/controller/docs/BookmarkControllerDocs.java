package project.flipnote.bookmark.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.bookmark.model.BookmarkTargetType;
import project.flipnote.common.model.response.IdResponse;
import project.flipnote.common.security.dto.AuthPrinciple;

@Tag(name = "Bookmark", description = "Bookmark API")
public interface BookmarkControllerDocs {

	@Operation(summary = "즐겨찾기 추가", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<IdResponse> addBookmark(BookmarkTargetType targetType, Long targetId, AuthPrinciple authPrinciple);
}
