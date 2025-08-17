package project.flipnote.common.model.request;

public record UserCreateCommand(
	String email,
	String name,
	String nickname,
	Boolean smsAgree,
	String phone,
	String profileImageUrl
) {

}
