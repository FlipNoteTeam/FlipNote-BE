package project.flipnote.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class EmailVerificationDto {

	public record Request(

		@Email @NotEmpty
		String email
	) {
	}
}
