package project.flipnote.groupapplication.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.groupapplication.model.*;
import project.flipnote.groupapplication.service.GroupApplicationService;

@RestController
@RequestMapping("/v1/group/{groupId}/joins")
@RequiredArgsConstructor
public class GroupApplicationController {

	private final GroupApplicationService groupApplicationService;

	//가입 신청 요청
	@PostMapping
	public ResponseEntity<GroupApplicationJoinResponse> joinRequest(
			UserAuth userAuth,
			@PathVariable("groupId") Long groupId,
			@Valid @RequestBody GroupApplicationJoinRequest req) {
		GroupApplicationJoinResponse res = groupApplicationService.joinRequest(userAuth, groupId, req);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	//그룹 내 가입 신청한 리스트 조회
	@GetMapping
	public ResponseEntity<GroupApplicationListResponse> findGroupJoinList(
			UserAuth userAuth,
			@PathVariable("groupId") Long groupId) {
		GroupApplicationListResponse res = groupApplicationService.findGroupJoinList(userAuth, groupId);

		return ResponseEntity.ok(res);
	}

	//가입 신청 응답
	@PatchMapping("/{joinId}")
	public ResponseEntity<GroupApplicationRespondResponse> respondToJoinRequest(
			UserAuth userAuth,
			@PathVariable("groupId") Long groupId,
			@PathVariable("joinId") Long joinId,
			@Valid @RequestBody GroupApplicationRespondRequest req) {

		GroupApplicationRespondResponse res = groupApplicationService.respondToJoinRequest(userAuth, groupId, joinId, req);

		return ResponseEntity.ok(res);
	}

}
