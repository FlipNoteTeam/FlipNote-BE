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
import project.flipnote.common.security.dto.AuthPrinciple;
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
	public ResponseEntity<Void> withdraw(@AuthenticationPrincipal AuthPrinciple userAuth) {
		userService.withdraw(userAuth.userId());
		return ResponseEntity.noContent().build();
	}

	@PutMapping
	public ResponseEntity<UserUpdateResponse> update(
		@AuthenticationPrincipal AuthPrinciple userAuth,
		@Valid @RequestBody UserUpdateRequest req
	) {
		UserUpdateResponse res = userService.update(userAuth.userId(), req);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/me")
	public ResponseEntity<MyInfoResponse> getMyInfo(
		@AuthenticationPrincipal AuthPrinciple userAuth
	) {
		MyInfoResponse res = userService.getMyInfo(userAuth.userId());
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
