package project.flipnote.common.dto;

public record UserCreateCommand(
	Long userId,
	String email,
	String name,
	String nickname,
	Boolean smsAgree,
	String phone,
	String profileImageUrl
) {

}
