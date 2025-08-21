package project.flipnote.notification.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.notification.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	@Query("SELECT n FROM Notification n WHERE (:cursor IS NULL OR n.id < :cursor) AND n.receiverId = :receiverId ORDER BY n.id DESC")
	List<Notification> findNotificationsByReceiverIdAndCursor(
		@Param("receiverId") Long receiverId,
		@Param("cursor") Long cursor,
		Pageable pageable
	);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		UPDATE Notification n
		   SET n.read = TRUE, n.readAt = :now
		 WHERE n.receiverId = :userId
		   AND n.id IN :ids
		   AND n.read is FALSE
		""")
	int bulkMarkAsRead(
		@Param("userId") Long userId,
		@Param("ids") List<Long> ids,
		@Param("now") LocalDateTime now
	);
}
