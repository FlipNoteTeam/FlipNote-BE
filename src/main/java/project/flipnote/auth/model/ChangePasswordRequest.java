package project.flipnote.auth.model;

import project.flipnote.common.validation.annotation.ValidPassword;

public record ChangePasswordRequest(

	@ValidPassword
	String currentPassword,

	@ValidPassword
	String newPassword
) {
}
