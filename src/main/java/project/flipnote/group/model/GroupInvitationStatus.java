package project.flipnote.group.model;

public enum GroupInvitationStatus {
	PENDING, ACCEPTED, REJECTED;

	public static GroupInvitationStatus from(project.flipnote.group.entity.GroupInvitationStatus status) {
		if (status == null) {
			throw new IllegalArgumentException("GroupInvitationStatus is null");
		}

		return switch (status) {
			case PENDING -> PENDING;
			case ACCEPTED -> ACCEPTED;
			case REJECTED -> REJECTED;
			default -> throw new IllegalArgumentException("Unknown GroupInvitationStatus: " + status);
		};
	}
}
