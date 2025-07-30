package project.flipnote.user.model;

import project.flipnote.user.entity.UserProfile;

public record UserUpdateResponse(
	Long userId,
	String nickname,
	String phone,
	Boolean smsAgree,
	String profileImageUrl
) {

	public static UserUpdateResponse from(UserProfile user) {
		return new UserUpdateResponse(
			user.getId(), user.getNickname(), user.getPhone(), user.isSmsAgree(), user.getProfileImageUrl()
		);
	}
}
