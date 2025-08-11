package project.flipnote.group.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.common.response.PageResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.model.GroupInvitationCreateResponse;
import project.flipnote.group.model.GroupInvitationRespondRequest;
import project.flipnote.group.model.GroupInvitationResponse;

@Tag(name = "Group Invitation", description = "Group Invitation API")
public interface GroupInvitationControllerDocs {

	@Operation(summary = "그룹 초대")
	ResponseEntity<GroupInvitationCreateResponse> createGroupInvitation(
		Long groupId, GroupInvitationCreateRequest req, AuthPrinciple authPrinciple
	);

	@Operation(summary = "그룹 초대 취소")
	ResponseEntity<Void> deleteGroupInvitation(Long groupId, Long invitationId, AuthPrinciple authPrinciple);

	@Operation(summary = "그룹 초대 응답")
	ResponseEntity<Void> respondToGroupInvitation(
		Long groupId, Long invitationId, GroupInvitationRespondRequest req, AuthPrinciple authPrinciple
	);

	@Operation(summary = "그룹 초대 목록 조회")
	ResponseEntity<PageResponse<GroupInvitationResponse>> getGroupInvitations(
		Long groupId,
		int page,
		int size,
		AuthPrinciple authPrinciple
	);
}
