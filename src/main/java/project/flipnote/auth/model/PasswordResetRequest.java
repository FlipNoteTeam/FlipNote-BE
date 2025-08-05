package project.flipnote.auth.model;

import jakarta.validation.constraints.NotBlank;
import project.flipnote.common.validation.annotation.ValidPassword;

public record PasswordResetRequest(

	@NotBlank
	String token,

	@ValidPassword
	String password
) {
}
