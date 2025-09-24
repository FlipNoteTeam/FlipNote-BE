package project.flipnote.auth.model.request;

import jakarta.validation.constraints.NotBlank;
import project.flipnote.common.validation.annotation.ValidPassword;

public record PasswordResetRequest(

	@NotBlank
	String token,

	@ValidPassword
	String password
) {
}
