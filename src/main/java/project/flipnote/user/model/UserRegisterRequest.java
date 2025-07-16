package project.flipnote.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import project.flipnote.common.util.PhoneUtil;
import project.flipnote.common.validation.annotation.ValidPassword;
import project.flipnote.common.validation.annotation.ValidPhone;

public record UserRegisterRequest(
	@Email @NotBlank
	String email,

	@ValidPassword
	String password,

	@NotBlank
	String name,

	@NotBlank
	String nickname,

	@NotNull
	Boolean smsAgree,

	@ValidPhone
	String phone,

	String profileImageUrl
) {

	public String getNormalizedPhone() {
		return PhoneUtil.normalize(phone);
	}
}
