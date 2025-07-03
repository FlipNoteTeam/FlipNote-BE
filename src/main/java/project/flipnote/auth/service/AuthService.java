package project.flipnote.auth.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.exception.AuthErrorCode;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.model.UserLoginDto;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.jwt.JwtComponent;
import project.flipnote.user.entity.User;
import project.flipnote.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtComponent jwtComponent;

	public TokenPair login(UserLoginDto.Request req) {
		User user = findByEmailOrThrow(req);

		if (!passwordEncoder.matches(req.password(), user.getPassword())) {
			throw new BizException(AuthErrorCode.INVALID_CREDENTIALS);
		}

		return jwtComponent.generateTokenPair(user.getEmail(), user.getId(), user.getRole().name());
	}

	private User findByEmailOrThrow(UserLoginDto.Request req) {
		return userRepository.findByEmail(req.email())
			.orElseThrow(() -> new BizException(AuthErrorCode.INVALID_CREDENTIALS));
	}
}
