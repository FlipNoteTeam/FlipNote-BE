package project.flipnote.cardset.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.cardset.model.CardSetSearchRequest;
import project.flipnote.cardset.model.CardSetSummaryResponse;
import project.flipnote.common.model.response.PagingResponse;

@Tag(name = "CardSet", description = "CardSet API")
public interface CardSetControllerDocs {

	@Operation(summary = "카드셋 목록 조회(검색)", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<PagingResponse<CardSetSummaryResponse>> getCardSets(
		CardSetSearchRequest req
	);
}
