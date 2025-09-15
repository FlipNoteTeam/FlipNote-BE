package project.flipnote.common.security.jwt;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class TokenIdGenerator {

	private final SecureRandom random = new SecureRandom();

	public String generate() {
		long value = Math.abs(random.nextLong());
		return Long.toString(value);
	}
}
