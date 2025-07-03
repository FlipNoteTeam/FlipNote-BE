package project.flipnote.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class UserLoginDto {

	public record Request(

		@Email @NotEmpty
		String email,

		@NotEmpty
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
