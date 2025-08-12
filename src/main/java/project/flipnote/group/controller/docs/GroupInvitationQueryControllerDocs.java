package project.flipnote.group.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.common.response.PageResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.model.IncomingGroupInvitationResponse;
import project.flipnote.group.model.OutgoingGroupInvitationResponse;

@Tag(name = "Group Invitation Query", description = "Group Invitation Query API")
public interface GroupInvitationQueryControllerDocs {

	@Operation(summary = "그룹 초대 보낸 목록 조회", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<PageResponse<OutgoingGroupInvitationResponse>> getOutgoingInvitations(
		Long groupId,
		int page,
		int size,
		AuthPrinciple authPrinciple
	);

	@Operation(summary = "그룹 초대 받은 목록 조회", security = {@SecurityRequirement(name = "access-token")})
	ResponseEntity<PageResponse<IncomingGroupInvitationResponse>> getIncomingInvitations(
		int page,
		int size,
		AuthPrinciple authPrinciple
	);
}
