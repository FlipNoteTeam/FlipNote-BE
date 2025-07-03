package project.flipnote.auth.model;

public record TokenPair(
	String accessToken,
	String refreshToken
) {

	public static TokenPair from(String accessToken, String refreshToken) {
		return new TokenPair(accessToken, refreshToken);
	}
}
