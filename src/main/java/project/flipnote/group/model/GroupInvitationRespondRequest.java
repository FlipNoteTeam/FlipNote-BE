package project.flipnote.group.model;

import jakarta.validation.constraints.NotNull;
import project.flipnote.group.entity.GroupInvitationStatus;

public record GroupInvitationRespondRequest(
	@NotNull
	GroupInvitationResponseStatus status
) {

	public GroupInvitationStatus toEntityStatus() {
		return switch (status) {
			case ACCEPTED -> GroupInvitationStatus.ACCEPTED;
			case REJECTED -> GroupInvitationStatus.REJECTED;
		};
	}
}
