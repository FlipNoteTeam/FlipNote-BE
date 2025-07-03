package project.flipnote.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.user.model.UserRegisterDto;
import project.flipnote.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserRegisterDto.Response> register(@Valid @RequestBody UserRegisterDto.Request req) {
		UserRegisterDto.Response res = userService.register(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(res);
	}
}
