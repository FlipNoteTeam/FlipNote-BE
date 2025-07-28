package project.flipnote.common.config;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.client")
public class ClientProperties {

	public enum PathKey {
		PASSWORD_RESET,
		SOCIAL_LINK_SUCCESS,
		SOCIAL_LINK_FAILURE,
		SOCIAL_LINK_CONFLICT,
		SOCIAL_LOGIN_SUCCESS,
		SOCIAL_LOGIN_FAILURE
	}

	private String url;
	private final Map<PathKey, String> paths = new EnumMap<>(PathKey.class);

	public String buildUrl(PathKey key, Object... uriVariables) {
		String path = paths.get(key);
		if (path == null) {
			throw new IllegalArgumentException("'" + key + "'에 해당하는 클라이언트 경로가 설정되지 않았습니다.");
		}

		return UriComponentsBuilder.fromUriString(url)
			.path(path)
			.buildAndExpand(uriVariables)
			.toUriString();
	}
}
