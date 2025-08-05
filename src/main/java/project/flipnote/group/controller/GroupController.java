package project.flipnote.group.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.UserPrincipal;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.service.GroupService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/groups")
public class GroupController {
	private final GroupService groupService;

	@PostMapping("")
	public ResponseEntity<GroupCreateResponse> create(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody GroupCreateRequest req) {
		GroupCreateResponse res = groupService.create(userPrincipal, req);
		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}
}
