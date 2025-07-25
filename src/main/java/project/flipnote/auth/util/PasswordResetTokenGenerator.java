package project.flipnote.auth.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class PasswordResetTokenGenerator {

	public String generateToken() {
		return UUID.randomUUID().toString();
	}
}
