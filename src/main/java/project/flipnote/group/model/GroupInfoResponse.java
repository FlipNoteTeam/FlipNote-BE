package project.flipnote.group.model;

public record GroupInfoResponse(
		Long groupId
) {
	public static GroupInfoResponse from(Long groupId) {
		return new GroupInfoResponse(groupId);
	}
}
