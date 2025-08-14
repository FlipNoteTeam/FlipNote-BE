package project.flipnote.notification.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.SendResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.response.CursorPageResponse;
import project.flipnote.group.service.GroupService;
import project.flipnote.infra.firebase.FcmErrorCode;
import project.flipnote.infra.firebase.FirebaseService;
import project.flipnote.notification.entity.FcmToken;
import project.flipnote.notification.entity.Notification;
import project.flipnote.notification.entity.NotificationType;
import project.flipnote.notification.exception.NotificationErrorCode;
import project.flipnote.notification.model.NotificationListRequest;
import project.flipnote.notification.model.NotificationResponse;
import project.flipnote.notification.model.TokenRegisterRequest;
import project.flipnote.notification.repository.FcmTokenRepository;
import project.flipnote.notification.repository.NotificationRepository;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final MessageSource messageSource;
	private final GroupService groupService;
	private final FcmTokenRepository fcmTokenRepository;
	private final FirebaseService firebaseService;

	public CursorPageResponse<NotificationResponse> getNotifications(Long userId, NotificationListRequest req) {
		Pageable pageable = PageRequest.of(0, req.getSize() + 1);
		List<Notification> notifications
			= notificationRepository.findNotificationsByReceiverIdAndCursor(userId, req.getCursorId(), pageable);

		boolean hasNext = notifications.size() > req.getSize();
		Long nextCursor = null;
		if (hasNext) {
			notifications = notifications.subList(0, req.getSize());
			nextCursor = notifications.get(notifications.size() - 1).getId();
		}

		List<NotificationResponse> content = notifications.stream()
			.map((notification -> {
				String message = buildMessage(notification, Locale.KOREA);
				return NotificationResponse.of(notification, message);
			}))
			.toList();

		return CursorPageResponse.of(content, hasNext, nextCursor);
	}

	@Transactional
	public void sendGroupInvite(Long groupId, Long inviteeId) {
		NotificationType type = NotificationType.GROUP_INVITE;
		String groupName = groupService.findGroupName(groupId);

		Notification notification = Notification.builder()
			.receiverId(inviteeId)
			.type(type)
			.variables(Map.of("groupName", groupName))
			.additionalData(Map.of("groupId", groupId))
			.build();
		notificationRepository.save(notification);

		String message = buildMessage(notification, Locale.KOREA);
		sendNotification(inviteeId, message);
	}

	@Transactional
	public void registerFcmToken(Long userId, TokenRegisterRequest req) {
		fcmTokenRepository.findByUserIdAndToken(userId, req.token())
			.ifPresentOrElse(
				FcmToken::updateLastUsedAt,
				() -> saveFcmToken(userId, req)
			);
	}

	private void sendNotification(Long userId, String body) {
		List<FcmToken> infos = fcmTokenRepository.findByUserId(userId);
		if (infos.isEmpty()) {
			log.warn("No FCM tokens for user {}", userId);
			return;
		}

		List<String> tokens = infos.stream().map(FcmToken::getToken).toList();
		try {
			BatchResponse response = firebaseService.sendEachForMulticast(tokens, "알림", body);

			List<String> validTokens = new ArrayList<>();
			List<String> invalidTokens = new ArrayList<>();
			for (int i = 0; i < response.getResponses().size(); i++) {
				SendResponse r = response.getResponses().get(i);
				if (r.isSuccessful()) {
					validTokens.add(tokens.get(i));
				} else {
					String errorName = r.getException().getMessagingErrorCode().name();
					FcmErrorCode code = FcmErrorCode.from(errorName);
					if (code == FcmErrorCode.UNREGISTERED || code == FcmErrorCode.INVALID_ARGUMENT) {
						invalidTokens.add(tokens.get(i));
					}
				}
			}

			if (!invalidTokens.isEmpty()) {
				fcmTokenRepository.deleteByUserIdAndTokenIn(userId, invalidTokens);
			}
			if (!validTokens.isEmpty()) {
				fcmTokenRepository.bulkUpdateLastUsedAt(validTokens, LocalDateTime.now());
			}
		} catch (FirebaseMessagingException e) {
			log.error("FCM 전송 실패 userId:{}", userId, e);
			FcmErrorCode code = FcmErrorCode.from(e.getErrorCode().name());
			if (code == FcmErrorCode.UNAVAILABLE) {
				throw new BizException(NotificationErrorCode.FCM_SERVER_UNAVAILABLE);
			}
			throw new BizException(NotificationErrorCode.FCM_INTERNAL_ERROR);
		}
	}

	private String buildMessage(Notification notification, Locale locale) {
		String key = notification.getType().getMessageKey();
		String template = messageSource.getMessage(key, null, locale);
		StringSubstitutor substitutor = new StringSubstitutor(notification.getVariables());
		return substitutor.replace(template);
	}

	private void saveFcmToken(Long userId, TokenRegisterRequest req) {
		FcmToken fcmToken = FcmToken.builder()
			.userId(userId)
			.token(req.token())
			.build();

		fcmTokenRepository.save(fcmToken);
	}
}
