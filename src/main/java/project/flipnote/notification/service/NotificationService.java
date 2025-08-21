package project.flipnote.notification.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
import project.flipnote.notification.model.MarkNotificationsAsReadRequest;
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

	/**
	 * 알림 목록 커서 기반 페이징으로 조회
	 *
	 * @param userId 알림 목록 조회하는 회원 ID
	 * @param req    알림 목록 조회를 위한 정보
	 * @return 커서 기반 페이징된 알림 목록
	 * @author 윤정환
	 */
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

	/**
	 * 그룹 초대 알림 전송
	 *
	 * @param groupId   초대 보낸 그룹 ID
	 * @param inviteeId 초대 받은 회원 ID
	 * @author 윤정환
	 */
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

	/**
	 * FCM Token 등록
	 *
	 * @param userId 토큰을 등록하는 회원 ID
	 * @param req    토큰 정보
	 * @author 윤정환
	 */
	@Transactional
	public void registerFcmToken(Long userId, TokenRegisterRequest req) {
		Optional<FcmToken> existingToken = fcmTokenRepository.findByToken(req.token());

		if (existingToken.isPresent()) {
			FcmToken token = existingToken.get();

			if (Objects.equals(token.getUserId(), userId)) {
				token.updateLastUsedAt();
			} else {
				fcmTokenRepository.deleteById(token.getId());
			}
		} else {
			saveFcmToken(userId, req.token());
		}
	}

	/**
	 * 여러 알림을 읽음 처리
	 *
	 * @param userId 알림 읽음 처리를 사용하는 회원 ID
	 * @param req    알림 읽음 처리를 위한 정보
	 * @author 윤정환
	 */
	@Transactional
	public void markNotificationsAsRead(Long userId, MarkNotificationsAsReadRequest req) {
		notificationRepository.bulkMarkAsRead(userId, req.notificationIds(), LocalDateTime.now());
	}

	/**
	 * FCM을 통해 실제 알림 전송
	 * <p>
	 * 반드시 트랜잭션이 적용된 public 메서드에서 호출해야 합니다.
	 * </p>
	 *
	 * @param userId 알림을 받을 회원 ID
	 * @param body   알림 내용
	 * @author 윤정환
	 */
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
			String errorName = e.getMessagingErrorCode() != null ? e.getMessagingErrorCode().name() : "INTERNAL";
			FcmErrorCode code = FcmErrorCode.from(errorName);
			if (code == FcmErrorCode.UNAVAILABLE) {
				throw new BizException(NotificationErrorCode.FCM_SERVER_UNAVAILABLE);
			}
			throw new BizException(NotificationErrorCode.FCM_INTERNAL_ERROR);
		}
	}

	/**
	 * 알림 내용을 템플릿을 통해 만들어줌
	 *
	 * @param notification 알림 정보
	 * @param locale       템플릿을 읽어올 로케일
	 * @return 템플릿 치환이 완료된 최종 알림 메시지 문자열
	 * @author 윤정환
	 */
	private String buildMessage(Notification notification, Locale locale) {
		String key = notification.getType().getMessageKey();
		String template = messageSource.getMessage(key, null, key, locale);
		StringSubstitutor substitutor = new StringSubstitutor(notification.getVariables());
		return substitutor.replace(template);
	}

	/**
	 * FCM Token을 저장
	 *
	 * @param userId 토큰을 저장하려는 회원 ID
	 * @param token  저장하려는 토큰
	 * @author 윤정환
	 */
	private void saveFcmToken(Long userId, String token) {
		FcmToken fcmToken = FcmToken.builder()
			.userId(userId)
			.token(token)
			.build();

		fcmTokenRepository.save(fcmToken);
	}
}
