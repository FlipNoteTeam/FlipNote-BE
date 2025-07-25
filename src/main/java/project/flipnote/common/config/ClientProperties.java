package project.flipnote.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.client")
@Component
public class ClientProperties {
	private String url;
	private String passwordResetPath;

	public String buildPasswordResetUrl(String token) {
		if (!StringUtils.hasText(token)) {
			throw new IllegalArgumentException("Token cannot be null or empty");
		}

		return UriComponentsBuilder.fromUriString(url)
			.path(passwordResetPath)
			.pathSegment(token)
			.build()
			.toUriString();
	}
}
