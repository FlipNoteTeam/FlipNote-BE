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

	public static MyInfoResponse from(UserProfile user) {
		return new MyInfoResponse(
			user.getId(),
			user.getEmail(),
			user.getNickname(),
			user.getName(),
			user.getPhone(),
			user.isSmsAgree(),
			user.getProfileImageUrl(),
			user.getCreatedAt(),
			user.getModifiedAt()
		);
	}
}
