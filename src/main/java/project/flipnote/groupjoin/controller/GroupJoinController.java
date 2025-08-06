package project.flipnote.groupjoin.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.groupjoin.model.*;
import project.flipnote.groupjoin.service.GroupJoinService;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class GroupJoinController {

	private final GroupJoinService groupJoinService;

	//가입 신청 요청
	@PostMapping("/groups/{groupId}/joins")
	public ResponseEntity<GroupJoinResponse> joinRequest(
			@AuthenticationPrincipal AuthPrinciple authPrinciple,
			@PathVariable("groupId") Long groupId,
			@Valid @RequestBody GroupJoinRequest req) {
		GroupJoinResponse res = groupJoinService.joinRequest(authPrinciple, groupId, req);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	//그룹 내 가입 신청한 리스트 조회
	@GetMapping("/groups/{groupId}/joins")
	public ResponseEntity<GroupJoinListResponse> findGroupJoinList(
			@AuthenticationPrincipal AuthPrinciple authPrinciple,
			@PathVariable("groupId") Long groupId) {
		GroupJoinListResponse res = groupJoinService.findGroupJoinList(authPrinciple, groupId);

		return ResponseEntity.ok(res);
	}

	//가입 신청 응답
	@PatchMapping("/groups/{groupId}/joins/{joinId}")
	public ResponseEntity<GroupJoinRespondResponse> respondToJoinRequest(
			@AuthenticationPrincipal AuthPrinciple authPrinciple,
			@PathVariable("groupId") Long groupId,
			@PathVariable("joinId") Long joinId,
			@Valid @RequestBody GroupJoinRespondRequest req) {

		GroupJoinRespondResponse res = groupJoinService.respondToJoinRequest(authPrinciple, groupId, joinId, req);

		return ResponseEntity.ok(res);
	}
	
	//가입 신청 삭제
	@DeleteMapping("/groups/{groupId}/joins/{joinId}")
	public ResponseEntity<Void> groupJoinDelete(
		@AuthenticationPrincipal AuthPrinciple authPrinciple,
		@PathVariable("groupId") Long groupId,
		@PathVariable("joinId") Long joinId
	) {
		groupJoinService.groupJoinDelete(authPrinciple, groupId, joinId);

		return ResponseEntity.noContent().build();
	}

	//내가 신청한 가입신청 리스트 조회
	@GetMapping("/groups/joins/me")
	public ResponseEntity<FindGroupJoinListMeResponse> findGroupJoinMe(
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		FindGroupJoinListMeResponse res = groupJoinService.findGroupJoinListMe(authPrinciple);

		return ResponseEntity.ok(res);
	}
}
