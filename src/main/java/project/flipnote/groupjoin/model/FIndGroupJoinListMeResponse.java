package project.flipnote.groupjoin.model;

import java.util.List;

import project.flipnote.groupjoin.entity.GroupJoin;

public record FIndGroupJoinListMeResponse(
	List<GroupJoin> groupJoins
) {
	public static FIndGroupJoinListMeResponse from(List<GroupJoin> groupJoins) {
		return new FIndGroupJoinListMeResponse(groupJoins);
	}
}
