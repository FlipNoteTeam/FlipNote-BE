package project.flipnote.groupapplication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import project.flipnote.groupapplication.model.GroupApplicationJoinRequestDto;
import project.flipnote.groupapplication.service.GroupApplicationService;

@RestController
@RequestMapping("/v1/group/{groupId}")
@RequiredArgsConstructor
public class GroupApplicationController {

	private final GroupApplicationService groupApplicationService;

	@PostMapping("/joins")
	public ResponseEntity<GroupApplicationJoinRequestDto.Response> joinRequest(@PathVariable("groupId") Long groupId, GroupApplicationJoinRequestDto.Request req) {
		GroupApplicationJoinRequestDto.Response res = groupApplicationService.joinRequest(1L, groupId, req);

		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}
}
