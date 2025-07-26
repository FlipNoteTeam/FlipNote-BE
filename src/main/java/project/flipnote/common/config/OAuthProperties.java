package project.flipnote.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@ConfigurationProperties(prefix = "app.oauth2")
public class OAuthProperties {

	private final Map<String, Provider> providers = new HashMap<>();

	@Getter
	@Setter
	public static class Provider {
		private String clientId;
		private String clientSecret;
		private String redirectUri;
		private String authorizationUri;
		private String tokenUri;
		private String userInfoUri;
		private List<String> scope;
	}
}