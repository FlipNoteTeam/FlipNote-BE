package project.flipnote.group.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.group.entity.GroupInvitation;
import project.flipnote.group.entity.GroupInvitationStatus;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

	Optional<GroupInvitation> findByIdAndStatus(Long id, GroupInvitationStatus status);

	Optional<GroupInvitation> findByIdAndGroup_IdAndInviteeUserIdAndStatus(
		Long id, Long groupId, Long inviteeUserId, GroupInvitationStatus status
	);

	Page<GroupInvitation> findAllByGroup_Id(Long groupId, Pageable pageable);

	Page<GroupInvitation> findAllByInviteeUserId(Long inviteeUserId, Pageable pageable);

	boolean existsByGroup_IdAndInviteeUserIdAndStatus(Long groupId, Long inviteeUserId, GroupInvitationStatus status);

	boolean existsByGroup_IdAndInviteeEmailAndStatus(Long groupId, String inviteeEmail, GroupInvitationStatus status);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		UPDATE GroupInvitation gi
		SET gi.status = project.flipnote.group.entity.GroupInvitationStatus.EXPIRED
		WHERE gi.status = project.flipnote.group.entity.GroupInvitationStatus.PENDING
		AND gi.expiredAt < :now
		""")
	int bulkExpire(@Param("now") LocalDateTime now);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		UPDATE GroupInvitation  gi
		SET gi.inviteeUserId = :inviteeUserId
		WHERE gi.inviteeEmail = :inviteeEmail
		""")
	int bulkUpdateInviteeUserId(@Param("inviteeEmail") String inviteeEmail, @Param("inviteeUserId") Long inviteeUserId);
}
