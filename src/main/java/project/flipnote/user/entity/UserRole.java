package project.flipnote.user.entity;

import java.util.Arrays;

public enum UserRole {
	USER, ADMIN;

	public static UserRole from(String role) {
		return Arrays.stream(UserRole.values())
			.filter(r -> r.name().equalsIgnoreCase(role))
			.findFirst()
			.orElse(null);
	}
}
