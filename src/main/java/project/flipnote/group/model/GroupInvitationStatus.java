package project.flipnote.group.model;

import project.flipnote.group.entity.GroupInvitation;

public enum GroupInvitationStatus {
	PENDING, ACCEPTED, REJECTED, EXPIRED;

	public static GroupInvitationStatus from(GroupInvitation invitation) {
		return GroupInvitationStatus.valueOf(invitation.getStatus().name());
	}
}
