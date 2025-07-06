package project.flipnote.group.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.group.model.GroupCreateDto;
import project.flipnote.group.service.GroupService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/groups")
public class GroupController {
	private final GroupService groupService;

	@PostMapping("")
	public ResponseEntity<GroupCreateDto.Response> create(@Valid @RequestBody GroupCreateDto.Request req) {
		GroupCreateDto.Response res = groupService.create(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}
}
