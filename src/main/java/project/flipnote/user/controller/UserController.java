package project.flipnote.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.user.model.MyInfoResponse;
import project.flipnote.user.model.ChangePasswordRequest;
import project.flipnote.user.model.SocialLinksResponse;
import project.flipnote.user.model.UserInfoResponse;
import project.flipnote.user.model.UserRegisterRequest;
import project.flipnote.user.model.UserRegisterResponse;
import project.flipnote.user.model.UserUpdateRequest;
import project.flipnote.user.model.UserUpdateResponse;
import project.flipnote.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest req) {
		UserRegisterResponse res = userService.register(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}

	@DeleteMapping
	public ResponseEntity<Void> unregister(@AuthenticationPrincipal UserAuth userAuth) {
		userService.unregister(userAuth.userId());
		return ResponseEntity.noContent().build();
	}

	@PutMapping
	public ResponseEntity<UserUpdateResponse> update(
		@AuthenticationPrincipal UserAuth userAuth,
		@Valid @RequestBody UserUpdateRequest req
	) {
		UserUpdateResponse res = userService.update(userAuth.userId(), req);
		return ResponseEntity.ok(res);
	}

	@GetMapping("/me")
	public ResponseEntity<MyInfoResponse> getMyInfo(
		@AuthenticationPrincipal UserAuth userAuth
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

	@PatchMapping("/me/password")
	public ResponseEntity<Void> updatePassword(
		@AuthenticationPrincipal UserAuth userAuth,
		@Valid @RequestBody ChangePasswordRequest req
	) {
		userService.changePassword(userAuth.userId(), req);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me/social-links")
	public ResponseEntity<SocialLinksResponse> getSocialLinks(
		@AuthenticationPrincipal UserAuth userAuth
	) {
		SocialLinksResponse res = userService.getSocialLinks(userAuth.userId());

		return ResponseEntity.ok(res);
	}
}
