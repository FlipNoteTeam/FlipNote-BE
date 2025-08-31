package project.flipnote.groupjoin.controller.docs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.groupjoin.model.FindGroupJoinListMeResponse;
import project.flipnote.groupjoin.model.GroupJoinListResponse;
import project.flipnote.groupjoin.model.GroupJoinRequest;
import project.flipnote.groupjoin.model.GroupJoinRespondRequest;
import project.flipnote.groupjoin.model.GroupJoinRespondResponse;
import project.flipnote.groupjoin.model.GroupJoinResponse;

public interface GroupJoinControllerDocs {
	//가입 신청 요청
	@Operation(summary = "가입 신청 요청", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<GroupJoinResponse> joinRequest(
		AuthPrinciple authPrinciple,
		Long groupId,
		GroupJoinRequest req
	);

	//그룹 내 가입 신청한 리스트 조회
	@Operation(summary = "그룹 내 가입 신청한 리스트 조회", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<GroupJoinListResponse> findGroupJoinList(
		AuthPrinciple authPrinciple,
		Long groupId
	);

	//가입 신청 응답
	@Operation(summary = "가입 신청 응답", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<GroupJoinRespondResponse> respondToJoinRequest(
		AuthPrinciple authPrinciple,
		Long groupId,
		Long joinId,
		GroupJoinRespondRequest req
	);

	//가입 신청 삭제
	@Operation(summary = "가입 신청 삭제", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<Void> groupJoinDelete(
		AuthPrinciple authPrinciple, Long groupId,
		Long joinId
	);

	//내가 신청한 가입신청 리스트 조회
	@Operation(summary = "내가 신청한 가입신청 리스트 조회", security = {@SecurityRequirement(name = "access-token")})
	public ResponseEntity<FindGroupJoinListMeResponse> findGroupJoinMe(
		AuthPrinciple authPrinciple
	);
}
