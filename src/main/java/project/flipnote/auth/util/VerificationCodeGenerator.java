package project.flipnote.auth.util;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGenerator {

	private static final SecureRandom random = new SecureRandom();

	public String generateVerificationCode(int length) {
		int origin = (int)Math.pow(10, length - 1);
		int bound = (int)Math.pow(10, length);
		return String.valueOf(random.nextInt(origin, bound));
	}
}
