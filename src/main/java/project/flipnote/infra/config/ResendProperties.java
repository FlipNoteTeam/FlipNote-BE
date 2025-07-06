package project.flipnote.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("resend")
@Component
public class ResendProperties {

	@NotEmpty
	private String fromEmail;

	@NotEmpty
	private String apiKey;
}
