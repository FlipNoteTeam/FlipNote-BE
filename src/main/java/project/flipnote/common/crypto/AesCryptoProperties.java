package project.flipnote.common.crypto;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app.encryption")
@Component
public class AesCryptoProperties {

	@NotBlank
	@Pattern(
		regexp = "^[A-Za-z0-9+/]{32}$",
		message = "암호화 키는 32자의 Base64 문자열이어야 합니다."
	)
	private String key;
}
