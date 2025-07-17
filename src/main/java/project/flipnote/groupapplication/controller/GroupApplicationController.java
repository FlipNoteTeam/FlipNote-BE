package project.flipnote.groupapplication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.groupapplication.model.GroupApplicationJoinRequest;
import project.flipnote.groupapplication.model.GroupApplicationJoinResponse;
import project.flipnote.groupapplication.model.GroupApplicationListResponse;
import project.flipnote.groupapplication.service.GroupApplicationService;

@RestController
@RequestMapping("/v1/group/{groupId}/joins")
@RequiredArgsConstructor
public class GroupApplicationController {

	private final GroupApplicationService groupApplicationService;

	//가입 신청 요청
	@PostMapping("")
	public ResponseEntity<GroupApplicationJoinResponse> joinRequest(UserAuth userAuth, @PathVariable("groupId") Long groupId, GroupApplicationJoinRequest req) {
		GroupApplicationJoinResponse res = groupApplicationService.joinRequest(userAuth, groupId, req);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	//그룹 내 가입 신청한 리스트 조회
	public ResponseEntity<GroupApplicationListResponse> findGroupJoinList(UserAuth userAuth, @PathVariable("groupId") Long groupId) {
		GroupApplicationListResponse res = groupApplicationService.findGroupJoinList(userAuth, groupId);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}


}
