package project.flipnote.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

@EnableConfigurationProperties({AsyncProperties.class, ClientProperties.class, OAuthProperties.class})
@Configuration
public class AppConfig {

	@Bean
	public RestClient restClient() {
		return RestClient.create();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}
