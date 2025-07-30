package project.flipnote.user.model;

import project.flipnote.user.entity.UserProfile;

public record UserInfoResponse(
	Long userId,
	String nickname,
	String profileImageUrl
) {

	public static UserInfoResponse from(UserProfile user) {
		return new UserInfoResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
	}
}
