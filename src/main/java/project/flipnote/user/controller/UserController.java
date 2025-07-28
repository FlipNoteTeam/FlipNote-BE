package project.flipnote.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.AccountAuth;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.UserInfoResponse;
import project.flipnote.user.model.UserUpdateRequest;
import project.flipnote.user.model.UserUpdateResponse;
import project.flipnote.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

	private final UserService userService;

	@DeleteMapping
	public ResponseEntity<Void> unregister(@AuthenticationPrincipal AccountAuth accountAuth) {
		userService.unregister(accountAuth.accountId());
		return ResponseEntity.noContent().build();
	}

	@PutMapping
	public ResponseEntity<UserUpdateResponse> update(
		@AuthenticationPrincipal AccountAuth accountAuth,
		@Valid @RequestBody UserUpdateRequest req
	) {
		UserUpdateResponse res = userService.update(accountAuth.accountId(), req);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/me")
	public ResponseEntity<MyInfoResponse> getMyInfo(
		@AuthenticationPrincipal AccountAuth accountAuth
	) {
		MyInfoResponse res = userService.getMyInfo(accountAuth.accountId());
		return ResponseEntity.ok(res);
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserInfoResponse> getUserInfo(
		@PathVariable("userId") Long userId
	) {
		UserInfoResponse res = userService.getUserInfo(userId);
		return ResponseEntity.ok(res);
	}
}
