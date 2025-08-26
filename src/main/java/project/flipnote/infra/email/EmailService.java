package project.flipnote.infra.email;

public interface EmailService {

	void sendEmailVerificationCode(String to, String code, int ttl);

	void sendPasswordResetLink(String to, String link, int ttl);

	void sendGuestGroupInvitation(String to, String groupName, String registerUrl);
}
