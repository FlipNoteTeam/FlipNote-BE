package project.flipnote.user.model;

import project.flipnote.user.entity.User;

public record UserInfoResponse(
	Long userId,
	String nickname,
	String profileImageUrl
) {

	public static UserInfoResponse from(User user) {
		return new UserInfoResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
	}
}
