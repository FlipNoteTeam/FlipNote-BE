package project.flipnote.group.model;

public record GroupCreateResponse(
		Long groupId
) {
	public static GroupCreateResponse from(Long groupId) {
		return new GroupCreateResponse(groupId);
	}
}
