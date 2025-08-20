package project.flipnote.group.model;

public record GroupPutResponse(
	Long groupId
) {
	public static GroupPutResponse from(Long groupId) {
		return new GroupPutResponse(groupId);
	}
}
