package project.flipnote.notification.controller.docs;

import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import project.flipnote.common.model.response.CursorPagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.notification.model.NotificationListRequest;
import project.flipnote.notification.model.NotificationResponse;
import project.flipnote.notification.model.TokenRegisterRequest;

@Tag(name = "Notification", description = "Notification API")
public interface NotificationControllerDocs {

	@Operation(summary = "알림 목록 조회")
	ResponseEntity<CursorPagingResponse<NotificationResponse>> getNotifications(
		NotificationListRequest req,
		AuthPrinciple authPrinciple
	);

	@Operation(summary = "FCM 토큰 등록")
	ResponseEntity<String> registerFcmToken(TokenRegisterRequest req, AuthPrinciple authPrinciple);

	@Operation(summary = "모든 알림 읽음 처리")
	ResponseEntity<Void> markAllNotificationsAsRead(AuthPrinciple authPrinciple);

	@Operation(summary = "알림 읽음 처리")
	ResponseEntity<Void> markNotificationAsRead(Long notificationId, AuthPrinciple authPrinciple);
}
