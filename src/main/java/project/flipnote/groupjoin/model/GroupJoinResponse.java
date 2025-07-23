package project.flipnote.groupjoin.model;

public record GroupJoinResponse(
		Long groupJoinId
) {
	public static GroupJoinResponse from(Long groupJoinId) {
		return new GroupJoinResponse(groupJoinId);
	}
}
