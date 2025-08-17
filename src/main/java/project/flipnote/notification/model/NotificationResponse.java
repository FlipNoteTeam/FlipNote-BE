package project.flipnote.notification.model;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import project.flipnote.notification.entity.Notification;

public record NotificationResponse(
	Long notificationId,
	String message,
	Map<String, Object> additionalData,
	boolean isRead,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime readAt,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt
) {

	public static NotificationResponse of(Notification notification, String message) {
		return new NotificationResponse(
			notification.getId(),
			message,
			notification.getAdditionalData(),
			notification.isRead(),
			notification.getReadAt(),
			notification.getCreatedAt()
		);
	}
}
