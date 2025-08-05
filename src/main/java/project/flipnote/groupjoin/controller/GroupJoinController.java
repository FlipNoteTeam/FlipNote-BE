package project.flipnote.groupjoin.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.UserPrincipal;
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
			UserPrincipal userPrincipal,
			@PathVariable("groupId") Long groupId,
			@Valid @RequestBody GroupJoinRequest req) {
		GroupJoinResponse res = groupJoinService.joinRequest(userPrincipal, groupId, req);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	//그룹 내 가입 신청한 리스트 조회
	@GetMapping("/groups/{groupId}/joins")
	public ResponseEntity<GroupJoinListResponse> findGroupJoinList(
			UserPrincipal userPrincipal,
			@PathVariable("groupId") Long groupId) {
		GroupJoinListResponse res = groupJoinService.findGroupJoinList(userPrincipal, groupId);

		return ResponseEntity.ok(res);
	}

	//가입 신청 응답
	@PatchMapping("/groups/{groupId}/joins/{joinId}")
	public ResponseEntity<GroupJoinRespondResponse> respondToJoinRequest(
			UserPrincipal userPrincipal,
			@PathVariable("groupId") Long groupId,
			@PathVariable("joinId") Long joinId,
			@Valid @RequestBody GroupJoinRespondRequest req) {

		GroupJoinRespondResponse res = groupJoinService.respondToJoinRequest(userPrincipal, groupId, joinId, req);

		return ResponseEntity.ok(res);
	}
	
	//가입 신청 삭제
	@DeleteMapping("/groups/{groupId}/joins/{joinId}")
	public ResponseEntity<Void> groupJoinDelete(
		UserPrincipal userPrincipal,
		@PathVariable("groupId") Long groupId,
		@PathVariable("joinId") Long joinId
	) {
		groupJoinService.groupJoinDelete(userPrincipal, groupId, joinId);

		return ResponseEntity.noContent().build();
	}

	//내가 신청한 가입신청 리스트 조회
	@GetMapping("/groups/joins/me")
	public ResponseEntity<FIndGroupJoinListMeResponse> findGroupJoinMe(
		UserPrincipal userPrincipal
	) {
		FIndGroupJoinListMeResponse res = groupJoinService.findGroupJoinListMe(userPrincipal);

		return ResponseEntity.ok(res);
	}
}
