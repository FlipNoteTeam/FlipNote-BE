package project.flipnote.user.model;

public record UserRegisterResponse(
	Long userId
) {

	public static UserRegisterResponse from(Long userId) {
		return new UserRegisterResponse(userId);
	}
}
