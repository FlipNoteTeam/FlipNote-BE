package project.flipnote.auth.model.request;

import project.flipnote.common.validation.annotation.ValidPassword;

public record ChangePasswordRequest(

	@ValidPassword
	String currentPassword,

	@ValidPassword
	String newPassword
) {
}
