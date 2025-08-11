package project.flipnote.group.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.response.PageResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.controller.docs.GroupInvitationControllerDocs;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.model.GroupInvitationCreateResponse;
import project.flipnote.group.model.GroupInvitationRespondRequest;
import project.flipnote.group.model.GroupInvitationResponse;
import project.flipnote.group.service.GroupInvitationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/groups/{groupId}/invitations")
public class GroupInvitationController implements GroupInvitationControllerDocs {

	private final GroupInvitationService groupInvitationService;

	@PostMapping
	public ResponseEntity<GroupInvitationCreateResponse> createGroupInvitation(
		@PathVariable("groupId") Long groupId,
		@Valid @RequestBody GroupInvitationCreateRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		GroupInvitationCreateResponse res = groupInvitationService.createGroupInvitation(authPrinciple, groupId, req);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	@DeleteMapping("/{invitationId}")
	public ResponseEntity<Void> deleteGroupInvitation(
		@PathVariable("groupId") Long groupId,
		@PathVariable("invitationId") Long invitationId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		groupInvitationService.deleteGroupInvitation(authPrinciple.userId(), groupId, invitationId);

		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{invitationId}")
	public ResponseEntity<Void> respondToGroupInvitation(
		@PathVariable("groupId") Long groupId,
		@PathVariable("invitationId") Long invitationId,
		@Valid @RequestBody GroupInvitationRespondRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		groupInvitationService.respondToGroupInvitation(authPrinciple.userId(), groupId, invitationId, req);

		return ResponseEntity.ok().build();
	}

	@GetMapping
	public ResponseEntity<PageResponse<GroupInvitationResponse>> getGroupInvitations(
		@PathVariable("groupId") Long groupId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		PageResponse<GroupInvitationResponse> res
			= groupInvitationService.getGroupInvitations(authPrinciple.userId(), groupId, page, size);

		return ResponseEntity.ok(res);
	}
}
