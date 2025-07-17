package project.flipnote.group.model;

public record GroupMemberInfoResponse(
		Long groupId
){
	public static GroupMemberInfoResponse from(Long groupId) {
		return new GroupMemberInfoResponse(groupId);
	}
}
