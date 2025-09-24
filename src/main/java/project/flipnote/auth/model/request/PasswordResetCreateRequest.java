package project.flipnote.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetCreateRequest(
	@Email @NotBlank
	String email
) {
}
