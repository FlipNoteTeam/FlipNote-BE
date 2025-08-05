package project.flipnote.user.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import project.flipnote.user.entity.UserProfile;

public record MyInfoResponse(
	Long userId,
	String email,
	String nickname,
	String name,
	String phone,
	Boolean smsAgree,
	String profileImageUrl,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime modifiedAt
) {

	public static MyInfoResponse from(UserProfile userProfile) {
		return new MyInfoResponse(
			userProfile.getId(),
			userProfile.getEmail(),
			userProfile.getNickname(),
			userProfile.getName(),
			userProfile.getPhone(),
			userProfile.isSmsAgree(),
			userProfile.getProfileImageUrl(),
			userProfile.getCreatedAt(),
			userProfile.getModifiedAt()
		);
	}
}
