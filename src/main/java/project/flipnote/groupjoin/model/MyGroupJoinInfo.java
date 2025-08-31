package project.flipnote.groupjoin.model;

import project.flipnote.groupjoin.entity.GroupJoinStatus;

public record MyGroupJoinInfo(
	Long groupJoinId,
	Long groupId,
	String groupName,
	String joinIntro,
	GroupJoinStatus status
) {
	public static MyGroupJoinInfo from(Long groupJoinId, Long groupId, String groupName, String joinIntro, GroupJoinStatus status) {
		return new MyGroupJoinInfo(groupJoinId, groupId, groupName,joinIntro,status);
	}
}
