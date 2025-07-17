package project.flipnote.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailVerificationRequest(

	@Email @NotBlank
	String email
) {
}
