package project.flipnote.groupjoin.model;

import project.flipnote.groupjoin.entity.GroupJoinStatus;

public record GroupJoinInfo(
	Long groupJoinId,
	Long userId,
	String nickname,
	String joinIntro,
	GroupJoinStatus status
	) {
	public static GroupJoinInfo from(Long groupJoinId, Long userId, String nickname, String joinIntro, GroupJoinStatus status) {
		return new GroupJoinInfo(groupJoinId, userId, nickname, joinIntro, status);
	}
}
