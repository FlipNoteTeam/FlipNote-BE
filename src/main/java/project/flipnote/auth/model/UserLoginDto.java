package project.flipnote.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserLoginDto {

	public record Request(

		@Email @NotBlank
		String email,

		@NotBlank
		String password
	) {
	}

	public record Response(
		String accessToken
	) {

		public static Response from(String accessToken) {
			return new Response(accessToken);
		}
	}
}
