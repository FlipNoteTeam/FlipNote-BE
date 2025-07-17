package project.flipnote.infra.email;

public interface EmailService {

	void sendEmailVerificationCode(String to, String code, int ttl);
}
