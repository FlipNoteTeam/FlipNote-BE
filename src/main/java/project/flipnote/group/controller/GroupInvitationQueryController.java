package project.flipnote.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.controller.docs.GroupInvitationQueryControllerDocs;
import project.flipnote.group.model.GroupInvitationListRequest;
import project.flipnote.group.model.IncomingGroupInvitationResponse;
import project.flipnote.group.model.OutgoingGroupInvitationResponse;
import project.flipnote.group.service.GroupInvitationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class GroupInvitationQueryController implements GroupInvitationQueryControllerDocs {

	private final GroupInvitationService groupInvitationService;

	@GetMapping("/groups/{groupId}/invitations")
	public ResponseEntity<PagingResponse<OutgoingGroupInvitationResponse>> getOutgoingInvitations(
		@PathVariable("groupId") Long groupId,
		@Valid @ModelAttribute GroupInvitationListRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		PagingResponse<OutgoingGroupInvitationResponse> res
			= groupInvitationService.getOutgoingInvitations(authPrinciple.userId(), groupId, req);

		return ResponseEntity.ok(res);
	}

	@GetMapping("/group-invitations")
	public ResponseEntity<PagingResponse<IncomingGroupInvitationResponse>> getIncomingInvitations(
		@Valid @ModelAttribute GroupInvitationListRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		PagingResponse<IncomingGroupInvitationResponse> res
			= groupInvitationService.getIncomingInvitations(authPrinciple.userId(), req);

		return ResponseEntity.ok(res);
	}
}
