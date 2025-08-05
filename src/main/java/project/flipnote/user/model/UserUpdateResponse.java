package project.flipnote.user.model;

import project.flipnote.user.entity.UserProfile;

public record UserUpdateResponse(
	Long userId,
	String nickname,
	String phone,
	Boolean smsAgree,
	String profileImageUrl
) {

	public static UserUpdateResponse from(UserProfile userProfile) {
		return new UserUpdateResponse(
			userProfile.getId(), userProfile.getNickname(), userProfile.getPhone(), userProfile.isSmsAgree(), userProfile.getProfileImageUrl()
		);
	}
}
