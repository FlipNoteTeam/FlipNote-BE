package project.flipnote.auth.entity;

import java.util.Arrays;

public enum AccountRole {
	USER, ADMIN;

	public static AccountRole from(String role) {
		return Arrays.stream(AccountRole.values())
			.filter(r -> r.name().equalsIgnoreCase(role))
			.findFirst()
			.orElse(null);
	}
}
