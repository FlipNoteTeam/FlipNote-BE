package project.flipnote.user.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import project.flipnote.common.util.StringUtil;
import project.flipnote.common.validation.annotation.ValidPhone;

public record UserUpdateRequest(

	@NotEmpty
	String nickname,

	@ValidPhone
	String phone,

	@NotNull
	Boolean smsAgree,

	String profileImageUrl
) {

	public String getCleanedPhone() {
		return StringUtil.cleanPhone(phone);
	}
}
