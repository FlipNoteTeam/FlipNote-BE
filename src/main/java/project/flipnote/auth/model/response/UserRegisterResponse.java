package project.flipnote.auth.model.response;

public record UserRegisterResponse(
	Long userId
) {

	public static UserRegisterResponse from(Long userId) {
		return new UserRegisterResponse(userId);
	}
}
