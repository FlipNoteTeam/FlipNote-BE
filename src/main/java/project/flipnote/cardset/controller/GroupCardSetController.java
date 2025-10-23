package project.flipnote.cardset.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.controller.docs.GroupCardSetControllerDocs;
import project.flipnote.cardset.model.CardSetDetailResponse;
import project.flipnote.cardset.model.CardSetSearchRequest;
import project.flipnote.cardset.model.CardSetSummaryResponse;
import project.flipnote.cardset.model.CardSetUpdateRequest;
import project.flipnote.cardset.model.CreateCardSetRequest;
import project.flipnote.cardset.model.CreateCardSetResponse;
import project.flipnote.cardset.service.CardSetService;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/groups/{groupId}/card-sets")
public class GroupCardSetController implements GroupCardSetControllerDocs {

	private final CardSetService cardSetService;

	@PostMapping("")
	public ResponseEntity<CreateCardSetResponse> createCardSet(
		@AuthenticationPrincipal AuthPrinciple authPrinciple,
		@PathVariable("groupId") Long groupId,
		@RequestBody @Valid CreateCardSetRequest req
	) {
		CreateCardSetResponse res = cardSetService.createCardSet(groupId, authPrinciple, req);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	@GetMapping("/{cardSetId}")
	public ResponseEntity<CardSetDetailResponse> getCardSet(
		@PathVariable("groupId") Long groupId,
		@PathVariable("cardSetId") Long cardSetId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		CardSetDetailResponse res = cardSetService.getCardSet(authPrinciple.userId(), groupId, cardSetId);

		return ResponseEntity.ok(res);
	}

	@PutMapping("/{cardSetId}")
	public ResponseEntity<CardSetDetailResponse> updateCardSet(
		@PathVariable("groupId") Long groupId,
		@PathVariable("cardSetId") Long cardSetId,
		@Valid @RequestBody CardSetUpdateRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		CardSetDetailResponse res = cardSetService.updateCardSet(authPrinciple.userId(), groupId, cardSetId, req);

		return ResponseEntity.ok(res);
	}

	@GetMapping
	public ResponseEntity<PagingResponse<CardSetSummaryResponse>> getCardSets(
		@PathVariable("groupId") Long groupId,
		@Valid @ModelAttribute CardSetSearchRequest req
	) {
		PagingResponse<CardSetSummaryResponse> res = cardSetService.getCardSets(groupId, req);

		return ResponseEntity.ok(res);
	}
}
