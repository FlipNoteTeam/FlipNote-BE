package project.flipnote.group.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.model.GroupInvitationCreateResponse;
import project.flipnote.group.model.GroupInvitationRespondRequest;

@Tag(name = "Group Invitation", description = "Group Invitation API")
public interface GroupInvitationControllerDocs {

	@Operation(summary = "그룹 초대", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<GroupInvitationCreateResponse> createGroupInvitation(
		Long groupId, GroupInvitationCreateRequest req, AuthPrinciple authPrinciple
	);

	@Operation(summary = "그룹 초대 취소", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<Void> deleteGroupInvitation(Long groupId, Long invitationId, AuthPrinciple authPrinciple);

	@Operation(summary = "그룹 초대 응답", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<Void> respondToGroupInvitation(
		Long groupId, Long invitationId, GroupInvitationRespondRequest req, AuthPrinciple authPrinciple
	);
}
