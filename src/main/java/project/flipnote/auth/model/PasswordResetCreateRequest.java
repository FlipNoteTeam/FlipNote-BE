package project.flipnote.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetCreateRequest(
	@Email @NotBlank
	String email
) {
}
