package project.flipnote.cardset.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.controller.docs.CardSetControllerDocs;
import project.flipnote.cardset.model.CardSetSearchRequest;
import project.flipnote.cardset.model.CardSetSummaryResponse;
import project.flipnote.cardset.service.CardSetService;
import project.flipnote.common.model.response.PagingResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/card-sets")
public class CardSetController implements CardSetControllerDocs {

	private final CardSetService cardSetService;

	@GetMapping
	public ResponseEntity<PagingResponse<CardSetSummaryResponse>> getCardSets(
		@Valid @ModelAttribute CardSetSearchRequest req
	) {
		PagingResponse<CardSetSummaryResponse> res = cardSetService.getCardSets(req);

		return ResponseEntity.ok(res);
	}
}
