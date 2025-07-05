package project.flipnote.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import project.flipnote.common.annotation.ValidPassword;

public class UserRegisterDto {

	public record Request(
		@Email @NotEmpty
		String email,

		@ValidPassword
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

		public String getCleanedPhone() {
			return phone == null ? null : phone.replaceAll("-", "");
		}
	}

	public record Response(
		Long userId
	) {

		public static Response from(Long userId) {
			return new Response(userId);
		}
	}
}
