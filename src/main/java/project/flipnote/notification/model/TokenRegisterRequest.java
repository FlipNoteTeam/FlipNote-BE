package project.flipnote.notification.model;

import jakarta.validation.constraints.NotEmpty;

public record TokenRegisterRequest(
	@NotEmpty
	String token
) {
}
