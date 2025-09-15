package project.flipnote.common.security.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import project.flipnote.auth.entity.AccountRole;
import project.flipnote.common.security.jwt.JwtConstants;

public record AuthPrinciple(
	Long authId,
	Long userId,
	String email,
	AccountRole role,
	long tokenVersion
) {

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
	}

	public static AuthPrinciple from(project.flipnote.auth.entity.UserAuth account) {
		return new AuthPrinciple(
			account.getId(), account.getUserId(), account.getEmail(), account.getRole(), account.getTokenVersion()
		);
	}

	public static AuthPrinciple from(Claims claims) {
		long authId = claims.get(JwtConstants.AUTH_ID, Long.class);
		long userId = claims.get(JwtConstants.USER_ID, Long.class);
		AccountRole userRole = AccountRole.from(
			claims.get(JwtConstants.ROLE, String.class)
		);
		String email = claims.getSubject();
		long tokenVersion = claims.get(JwtConstants.TOKEN_VERSION, Long.class);

		return new AuthPrinciple(authId, userId, email, userRole, tokenVersion);
	}
}
