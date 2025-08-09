package project.flipnote.group.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record GroupInvitationCreateRequest(
	@Email @NotBlank
	String email
) {
}
