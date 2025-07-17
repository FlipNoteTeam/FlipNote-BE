package project.flipnote.user.model;

import project.flipnote.user.entity.User;

public record UserUpdateResponse(
	Long userId,
	String nickname,
	String phone,
	Boolean smsAgree,
	String profileImageUrl
) {

	public static UserUpdateResponse from(User user) {
		return new UserUpdateResponse(
			user.getId(), user.getNickname(), user.getPhone(), user.isSmsAgree(), user.getProfileImageUrl()
		);
	}
}
