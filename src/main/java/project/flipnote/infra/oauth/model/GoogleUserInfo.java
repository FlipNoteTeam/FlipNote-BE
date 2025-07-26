package project.flipnote.infra.oauth.model;

import java.util.Map;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

	private final Map<String, Object> attributes;

	@Override
	public String getProviderId() {
		return String.valueOf(attributes.get("sub"));
	}

	@Override
	public String getProvider() {
		return "google";
	}

	@Override
	public String getEmail() {
		return String.valueOf(attributes.get("email"));
	}

	@Override public String getName() {
		return String.valueOf(attributes.get("name"));
	}
}
