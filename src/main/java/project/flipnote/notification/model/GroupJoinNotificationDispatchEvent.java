package project.flipnote.notification.model;

import java.util.List;

import project.flipnote.notification.entity.Notification;

public record GroupJoinNotificationDispatchEvent(
	List<Notification> notifications
) {
}
