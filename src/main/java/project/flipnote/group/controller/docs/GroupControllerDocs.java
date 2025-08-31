package project.flipnote.group.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import project.flipnote.common.model.response.CursorPagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.model.FindGroupMemberResponse;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.model.GroupDetailResponse;
import project.flipnote.group.model.GroupInfo;
import project.flipnote.group.model.GroupListRequest;
import project.flipnote.group.model.GroupPutRequest;
import project.flipnote.group.model.GroupPutResponse;

public interface GroupControllerDocs {
	//그룹 생성
	@Operation(summary = "그룹 생성", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<GroupCreateResponse> create(
		AuthPrinciple authPrinciple,
		GroupCreateRequest req
	);

	//그룹 수정
	@Operation(summary = "그룹 수정", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<GroupPutResponse> changeGroup(
		AuthPrinciple authPrinciple, GroupPutRequest req,
		Long groupId
	);

	//그룹 상세
	@Operation(summary = "그룹 상세", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<GroupDetailResponse> findGroupDetail(
		AuthPrinciple authPrinciple,
		Long groupId
	);

	//그룹 삭제
	@Operation(summary = "그룹 삭제", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<Void> deleteGroup(
		AuthPrinciple authPrinciple,
		Long groupId
	);

	//그룹내 멤버 조회
	@Operation(summary = "그룹내 멤버 조회", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<FindGroupMemberResponse> findGroupMembers(
		AuthPrinciple authPrinciple,
		Long groupId
	);

	//그룹 전체 조회
	@Operation(summary = "그룹 전체 조회", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<CursorPagingResponse<GroupInfo>> findGroup(
		AuthPrinciple authPrinciple,
		GroupListRequest req
	);
}
