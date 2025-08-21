package project.flipnote.notification.model;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record MarkNotificationsAsReadRequest(
	@NotEmpty
	List<Long> notificationIds
) {
}
