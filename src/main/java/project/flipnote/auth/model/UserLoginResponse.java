package project.flipnote.auth.model;

public record UserLoginResponse(
	String accessToken
) {

	public static UserLoginResponse from(String accessToken) {
		return new UserLoginResponse(accessToken);
	}
}
