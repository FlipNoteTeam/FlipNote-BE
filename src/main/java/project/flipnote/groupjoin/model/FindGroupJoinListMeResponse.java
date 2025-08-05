package project.flipnote.groupjoin.model;

import java.util.List;

import project.flipnote.groupjoin.entity.GroupJoin;

public record FindGroupJoinListMeResponse(
	List<GroupJoin> groupJoins
) {
	public static FindGroupJoinListMeResponse from(List<GroupJoin> groupJoins) {
		return new FindGroupJoinListMeResponse(groupJoins);
	}
}
