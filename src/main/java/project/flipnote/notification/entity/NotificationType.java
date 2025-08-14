package project.flipnote.notification.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
	GROUP_INVITE("notification.group.invite");

	private final String messageKey;
}
