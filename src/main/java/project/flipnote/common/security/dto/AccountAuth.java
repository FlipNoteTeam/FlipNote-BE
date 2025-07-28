package project.flipnote.common.security.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import project.flipnote.auth.entity.AccountRole;
import project.flipnote.auth.entity.AuthAccount;
import project.flipnote.common.security.jwt.JwtConstants;

public record AccountAuth(
	Long accountId,
	String email,
	AccountRole userRole,
	long tokenVersion
) {

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name()));
	}

	public static AccountAuth from(AuthAccount account) {
		return new AccountAuth(account.getId(), account.getEmail(), account.getRole(), account.getTokenVersion());
	}

	public static AccountAuth from(Claims claims) {
		long userId = Long.parseLong(claims.getId());
		AccountRole userRole = AccountRole.from(
			claims.get(JwtConstants.ROLE, String.class)
		);
		String email = claims.getSubject();
		long tokenVersion = claims.get(JwtConstants.TOKEN_VERSION, Long.class);

		return new AccountAuth(userId, email, userRole, tokenVersion);
	}
}
