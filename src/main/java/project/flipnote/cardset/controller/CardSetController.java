package project.flipnote.cardset.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.model.CreateCardSetRequest;
import project.flipnote.cardset.model.CreateCardSetResponse;
import project.flipnote.cardset.service.CardSetService;
import project.flipnote.common.security.dto.AuthPrinciple;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/groups/{groupId}/card-sets")
public class CardSetController {

	private final CardSetService cardSetService;

	@PostMapping("")
	public ResponseEntity<CreateCardSetResponse> createCardSet(
		@AuthenticationPrincipal AuthPrinciple authPrinciple,
		@PathVariable("groupId") Long groupId,
		@RequestBody @Valid CreateCardSetRequest req
	) {
		CreateCardSetResponse res = cardSetService.createCardSet(groupId, authPrinciple, req);

		return null;
	}
}
