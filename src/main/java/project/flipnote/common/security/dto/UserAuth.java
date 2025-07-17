package project.flipnote.common.security.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import project.flipnote.common.security.jwt.JwtConstants;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserRole;

public record UserAuth(
	Long userId,
	String email,
	UserRole userRole,
	long tokenVersion
) {

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
	}

	public static UserAuth from(User user) {
		return new UserAuth(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());
	}

	public static UserAuth from(Claims claims) {
		long userId = Long.parseLong(claims.getId());
		UserRole userRole = UserRole.from(
			claims.get(JwtConstants.ROLE, String.class)
		);
		String email = claims.getSubject();
		long tokenVersion = claims.get(JwtConstants.TOKEN_VERSION, Long.class);

		return new UserAuth(userId, email, userRole, tokenVersion);
	}
}
