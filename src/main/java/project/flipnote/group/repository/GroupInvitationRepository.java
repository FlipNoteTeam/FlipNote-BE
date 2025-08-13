package project.flipnote.group.repository;

import java.time.LocalDateTime;
import java.util.List;
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

	@Query("""
		SELECT gi
		FROM GroupInvitation gi
		JOIN FETCH gi.group g
		WHERE gi.id = :id
		  AND g.id = :groupId
		  AND gi.inviteeUserId = :inviteeUserId
		  AND gi.status = :status
		""")
	Optional<GroupInvitation> findWithGroupByIdAndGroup_IdAndInviteeUserIdAndStatus(
		@Param("id") Long id,
		@Param("groupId") Long groupId,
		@Param("inviteeUserId") Long inviteeUserId,
		@Param("status") GroupInvitationStatus status
	);

	Page<GroupInvitation> findAllByGroup_Id(Long groupId, Pageable pageable);

	@Query("""
		SELECT gi
		FROM GroupInvitation gi
		JOIN FETCH gi.group g
		WHERE gi.inviteeEmail = :inviteeEmail
		  AND gi.status = :status
		""")
	List<GroupInvitation> findAllWithGroupByInviteeEmailAndStatus(
		@Param("inviteeEmail") String inviteeEmail,
		@Param("status") GroupInvitationStatus status
	);

	Page<GroupInvitation> findAllByInviteeUserId(Long inviteeUserId, Pageable pageable);

	boolean existsByGroup_IdAndInviteeUserIdAndStatus(Long groupId, Long inviteeUserId, GroupInvitationStatus status);

	boolean existsByGroup_IdAndInviteeEmailAndStatus(Long groupId, String inviteeEmail, GroupInvitationStatus status);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE GroupInvitation gi " +
		"SET gi.status = project.flipnote.group.entity.GroupInvitationStatus.EXPIRED " +
		"WHERE gi.status = project.flipnote.group.entity.GroupInvitationStatus.PENDING " +
		"AND gi.expiredAt < :now")
	int bulkExpire(@Param("now") LocalDateTime now);
}
