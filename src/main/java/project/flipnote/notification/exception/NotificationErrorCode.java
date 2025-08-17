package project.flipnote.notification.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
	FCM_INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "NOTIFICATION_001", "FCM 내부 오류가 발생했습니다"),
	FCM_SERVER_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "NOTIFICATION_002", "FCM 서버를 사용할 수 없습니다."),
	NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_003", "알림이 존재하지 않습니다."),
	ALREADY_READ_NOTIFICATION(HttpStatus.CONFLICT, "NOTIFICATION_004", "이미 읽은 알림입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
