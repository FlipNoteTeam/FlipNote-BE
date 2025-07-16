package project.flipnote.common.security.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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
}
