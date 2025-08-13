package project.flipnote.group.model;

public enum GroupInvitationStatus {
	PENDING, ACCEPTED, REJECTED, EXPIRED;

	public static GroupInvitationStatus from(project.flipnote.group.entity.GroupInvitationStatus status) {
		if (status == null) {
			throw new IllegalArgumentException("GroupInvitationStatus is null");
		}

		return switch (status) {
			case PENDING -> PENDING;
			case ACCEPTED -> ACCEPTED;
			case REJECTED -> REJECTED;
			case EXPIRED -> EXPIRED;
			default -> throw new IllegalArgumentException("Unknown GroupInvitationStatus: " + status);
		};
	}
}
