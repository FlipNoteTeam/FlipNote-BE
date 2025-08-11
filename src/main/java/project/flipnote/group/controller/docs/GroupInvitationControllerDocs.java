package project.flipnote.group.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.model.GroupInvitationCreateRequest;
import project.flipnote.group.model.GroupInvitationCreateResponse;

@Tag(name = "Group Invitation", description = "Group Invitation API")
public interface GroupInvitationControllerDocs {

	@Operation(summary = "그룹 초대")
	ResponseEntity<GroupInvitationCreateResponse> createGroupInvitation(
		Long groupId, GroupInvitationCreateRequest req, AuthPrinciple authPrinciple
	);

	@Operation(summary = "그룹 초대 취소")
	ResponseEntity<Void> deleteGroupInvitation(Long groupId, Long invitationId, AuthPrinciple authPrinciple);
}
