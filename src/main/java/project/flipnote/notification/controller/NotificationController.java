package project.flipnote.notification.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.model.response.CursorPagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.notification.controller.docs.NotificationControllerDocs;
import project.flipnote.notification.model.NotificationListRequest;
import project.flipnote.notification.model.NotificationResponse;
import project.flipnote.notification.model.TokenRegisterRequest;
import project.flipnote.notification.service.NotificationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/notifications")
public class NotificationController implements NotificationControllerDocs {

	private final NotificationService notificationService;

	@GetMapping
	public ResponseEntity<CursorPagingResponse<NotificationResponse>> getNotifications(
		@Valid @ModelAttribute NotificationListRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		CursorPagingResponse<NotificationResponse> res
			= notificationService.getNotifications(authPrinciple.userId(), req);

		return ResponseEntity.ok(res);
	}

	@PostMapping("/token")
	public ResponseEntity<String> registerFcmToken(
		@Valid @RequestBody TokenRegisterRequest req,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		notificationService.registerFcmToken(authPrinciple.userId(), req);

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/read-all")
	public ResponseEntity<Void> markAllNotificationsAsRead(
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		notificationService.markAllNotificationsAsRead(authPrinciple.userId());

		return ResponseEntity.ok().build();
	}

	@PostMapping("/{notificationId}/read")
	public ResponseEntity<Void> markNotificationAsRead(
		@PathVariable("notificationId") Long notificationId,
		@AuthenticationPrincipal AuthPrinciple authPrinciple
	) {
		notificationService.markNotificationAsRead(authPrinciple.userId(), notificationId);

		return ResponseEntity.ok().build();
	}
}
