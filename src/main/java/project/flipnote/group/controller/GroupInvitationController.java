package project.flipnote.group.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.controller.docs.GroupInvitationControllerDocs;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.service.GroupInvitationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/groups/{groupId}/invitations")
public class GroupInvitationController implements GroupInvitationControllerDocs {

	private final GroupInvitationService groupInvitationService;

	@PostMapping
	public ResponseEntity<Void> createGroupInvitation(
		@PathVariable("groupId") Long groupId,
		@Valid @RequestBody GroupInvitationCreateRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		groupInvitationService.createGroupInvitation(authPrinciple, groupId, req);

		return ResponseEntity.ok().build();
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
}
