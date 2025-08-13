package project.flipnote.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.response.PageResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.controller.docs.GroupInvitationQueryControllerDocs;
import project.flipnote.group.model.IncomingGroupInvitationResponse;
import project.flipnote.group.model.OutgoingGroupInvitationResponse;
import project.flipnote.group.service.GroupInvitationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class GroupInvitationQueryController implements GroupInvitationQueryControllerDocs {

	private final GroupInvitationService groupInvitationService;

	@GetMapping("/groups/{groupId}/invitations")
	public ResponseEntity<PageResponse<OutgoingGroupInvitationResponse>> getOutgoingInvitations(
		@PathVariable("groupId") Long groupId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		PageResponse<OutgoingGroupInvitationResponse> res
			= groupInvitationService.getOutgoingInvitations(authPrinciple.userId(), groupId, page, size);

		return ResponseEntity.ok(res);
	}

	@GetMapping("/group-invitations")
	public ResponseEntity<PageResponse<IncomingGroupInvitationResponse>> getIncomingInvitations(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		PageResponse<IncomingGroupInvitationResponse> res
			= groupInvitationService.getIncomingInvitations(authPrinciple.userId(), page, size);

		return ResponseEntity.ok(res);
	}
}
