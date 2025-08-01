package project.flipnote.infra.oauth.model;

public interface OAuth2UserInfo {
	String getProviderId();

	String getProvider();

	String getEmail();

	String getName();
}
