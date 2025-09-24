package project.flipnote.auth.model.response;

public record UserLoginResponse(
	String accessToken
) {

	public static UserLoginResponse from(String accessToken) {
		return new UserLoginResponse(accessToken);
	}
}
