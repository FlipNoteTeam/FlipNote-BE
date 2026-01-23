package project.flipnote.cardset.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.cardset.model.CardSetDetailResponse;
import project.flipnote.cardset.model.CardSetSearchRequest;
import project.flipnote.cardset.model.CardSetSummaryResponse;
import project.flipnote.cardset.model.CardSetUpdateRequest;
import project.flipnote.cardset.model.CreateCardSetRequest;
import project.flipnote.cardset.model.CreateCardSetResponse;
import project.flipnote.common.model.response.IdResponse;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;

@Tag(name = "CardSet", description = "CardSet API")
public interface GroupCardSetControllerDocs {

	@Operation(summary = "카드셋 생성", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<CreateCardSetResponse> createCardSet(
		AuthPrinciple authPrinciple, Long groupId, CreateCardSetRequest req
	);

	@Operation(summary = "카드셋 상세 조회", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<CardSetDetailResponse> getCardSet(Long groupId, Long cardSetId, AuthPrinciple authPrinciple);

	@Operation(summary = "카드셋 수정", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<CardSetDetailResponse> updateCardSet(
		Long groupId,
		Long cardSetId,
		CardSetUpdateRequest req,
		AuthPrinciple authPrinciple
	);

	@Operation(summary = "그룹별 카드셋 조회", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<PagingResponse<CardSetSummaryResponse>> getCardSets(Long groupId, CardSetSearchRequest req);

	@Operation(summary = "카드셋 삭제", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<IdResponse> deleteCardSet(Long groupId, Long cardSetId, AuthPrinciple authPrinciple);
}
