package project.flipnote.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("resend")
@Component
public class ResendProperties {
	private String fromEmail;
	private String apiKey;
}
