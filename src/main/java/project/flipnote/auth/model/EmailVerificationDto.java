package project.flipnote.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailVerificationDto {

	public record Request(

		@Email @NotBlank
		String email
	) {
	}
}
