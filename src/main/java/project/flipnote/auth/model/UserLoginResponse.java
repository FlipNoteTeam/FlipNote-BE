package project.flipnote.auth.model;

public record UserLoginResponse(
	String accessToken
) {

	public static UserLoginDto.Response from(String accessToken) {
		return new UserLoginDto.Response(accessToken);
	}
}
