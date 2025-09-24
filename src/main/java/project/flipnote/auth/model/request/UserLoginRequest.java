package project.flipnote.auth.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import project.flipnote.common.validation.annotation.ValidPassword;

public record UserLoginRequest(

	@Email @NotBlank
	String email,

	@ValidPassword
	String password
) {
}
