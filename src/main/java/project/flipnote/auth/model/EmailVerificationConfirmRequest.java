package project.flipnote.auth.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import project.flipnote.auth.constants.VerificationConstants;

public record EmailVerificationConfirmRequest(

	@Email @NotBlank
	String email,

	@NotBlank
	@Size(min = VerificationConstants.CODE_LENGTH, max = VerificationConstants.CODE_LENGTH)
	String code
) {
}
