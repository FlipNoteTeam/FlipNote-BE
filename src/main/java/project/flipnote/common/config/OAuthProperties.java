package project.flipnote.common.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Validated
@ConfigurationProperties(prefix = "app.oauth2")
public class OAuthProperties {

	@Valid
	private final Map<String, Provider> providers = new HashMap<>();


	@Getter
	@Setter
	@Valid
	public static class Provider {

		@NotBlank
		private String clientId;

		@NotBlank
		private String clientSecret;

		@NotBlank
		private String redirectUri;

		@NotBlank
		private String authorizationUri;

		@NotBlank
		private String tokenUri;

		@NotBlank
		private String userInfoUri;

		@NotNull
		private List<String> scope;
	}
}