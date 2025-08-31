package project.flipnote.groupjoin.model;

import java.util.List;

import project.flipnote.groupjoin.entity.GroupJoin;

public record FindGroupJoinListMeResponse(
	List<MyGroupJoinInfo> groupJoins
) {
	public static FindGroupJoinListMeResponse from(List<MyGroupJoinInfo> groupJoins) {
		return new FindGroupJoinListMeResponse(groupJoins);
	}
}
