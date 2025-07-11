package project.flipnote.common.crypto;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class AesCryptoConfig {

	private final AesCryptoProperties aesCryptoProperties;

	@Bean
	public SecretKeySpec aesSecretKeySpec() {
		return new SecretKeySpec(aesCryptoProperties.getKey().getBytes(StandardCharsets.UTF_8), "AES");
	}
}
