package project.flipnote.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UserRegisterDto {

	public record Request(
		@Email @NotEmpty
		String email,

		@NotEmpty
		String password,

		@NotEmpty
		String name,

		@NotEmpty
		String nickname,

		@NotNull
		Boolean smsAgree,

		@NotEmpty
		String phone,

		String profileImageUrl
	) {
	}

	public record Response(
		Long userId
	) {

		public static Response from(Long userId) {
			return new Response(userId);
		}
	}
}
