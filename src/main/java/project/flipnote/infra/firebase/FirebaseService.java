package project.flipnote.infra.firebase;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FirebaseService {

	@Value("${firebase.config-path}")
	private String firebaseConfigPath;

	private final Environment environment;

	@PostConstruct
	public void initialize() throws IOException {
		if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
			return;
		}

		if (FirebaseApp.getApps().isEmpty()) {
			try (FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath)) {
				FirebaseOptions options = FirebaseOptions.builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.build();
				FirebaseApp.initializeApp(options);
			}
		}
	}

	public BatchResponse sendEachForMulticast(
		List<String> tokens,
		String title,
		String body
	) throws FirebaseMessagingException {
		Notification notification = Notification.builder()
			.setTitle(title)
			.setBody(body)
			.build();
		MulticastMessage message = MulticastMessage.builder()
			.addAllTokens(tokens)
			.setNotification(notification)
			.build();

		return FirebaseMessaging.getInstance().sendEachForMulticast(message);
	}
}
