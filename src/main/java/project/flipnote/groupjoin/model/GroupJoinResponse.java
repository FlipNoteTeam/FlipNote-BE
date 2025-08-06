package project.flipnote.groupjoin.model;

import project.flipnote.groupjoin.entity.GroupJoinStatus;

public record GroupJoinResponse(
		Long groupJoinId,
		GroupJoinStatus status) {
	public static GroupJoinResponse from(Long groupJoinId, GroupJoinStatus status) {
		return new GroupJoinResponse(groupJoinId, status);
	}
}
