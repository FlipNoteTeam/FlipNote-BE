package project.flipnote.groupapplication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.groupapplication.model.GroupApplicationJoinRequest;
import project.flipnote.groupapplication.model.GroupApplicationJoinResponse;
import project.flipnote.groupapplication.service.GroupApplicationService;

@RestController
@RequestMapping("/v1/group/{groupId}")
@RequiredArgsConstructor
public class GroupApplicationController {

	private final GroupApplicationService groupApplicationService;

	//가입 신청 요청
	@PostMapping("/joins")
	public ResponseEntity<GroupApplicationJoinResponse> joinRequest(UserAuth userAuth, @PathVariable("groupId") Long groupId, GroupApplicationJoinRequest req) {
		GroupApplicationJoinResponse res = groupApplicationService.joinRequest(userAuth, groupId, req);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	//가입 신청 리스트 조회
}
