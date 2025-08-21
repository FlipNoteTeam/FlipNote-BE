package project.flipnote.group.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.model.GroupDetailResponse;
import project.flipnote.group.model.GroupPutRequest;
import project.flipnote.group.model.GroupPutResponse;
import project.flipnote.group.service.GroupService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/groups")
public class GroupController {
	private final GroupService groupService;

	//그룹 생성 API
	@PostMapping("")
	public ResponseEntity<GroupCreateResponse> create(
		@AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Valid @RequestBody GroupCreateRequest req) {
		GroupCreateResponse res = groupService.create(authPrinciple, req);
		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	//그룹 수정
	@PutMapping("/{groupId}")
	public ResponseEntity<GroupPutResponse> changeGroup(
		@AuthenticationPrincipal AuthPrinciple authPrinciple,
		@Valid @RequestBody GroupPutRequest req,
		@PathVariable("groupId") Long groupId) {
		GroupPutResponse res = groupService.changeGroup(authPrinciple, req, groupId);
		return ResponseEntity.ok(res);
	}

	//그룹 상세 API
	@GetMapping("/{groupId}")
	public ResponseEntity<GroupDetailResponse> findGroupDetail(
		@AuthenticationPrincipal AuthPrinciple authPrinciple,
		@PathVariable("groupId") Long groupId) {
		GroupDetailResponse res = groupService.findGroupDetail(authPrinciple, groupId);

		return ResponseEntity.ok(res);
	}

	//그룹 삭제 API
	@DeleteMapping("/{groupId}")
	public ResponseEntity<Void> deleteGroup(
		@AuthenticationPrincipal AuthPrinciple authPrinciple,
		@PathVariable("groupId") Long groupId
	) {
		groupService.deleteGroup(authPrinciple, groupId);

		return ResponseEntity.noContent().build();
	}
}
