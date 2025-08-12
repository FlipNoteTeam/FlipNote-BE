package project.flipnote.group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.group.entity.GroupInvitation;
import project.flipnote.group.entity.GroupInvitationStatus;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

	boolean existsByGroupIdAndInviteeUserId(Long groupId, Long inviteeUserId);

	boolean existsByGroupIdAndInviteeEmail(Long groupId, String inviteeEmail);

	Optional<GroupInvitation> findByIdAndStatus(Long id, GroupInvitationStatus status);

	Optional<GroupInvitation> findByIdAndGroupIdAndInviteeUserIdAndStatus(Long id, Long groupId, Long inviteeUserId, GroupInvitationStatus status);

	Page<GroupInvitation> findAllByGroupId(Long groupId, Pageable pageable);

	List<GroupInvitation> findAllByInviteeEmailAndStatus(String InviteeEmail, GroupInvitationStatus status);

	Page<GroupInvitation> findAllByInviteeUserId(Long inviteeUserId, Pageable pageable);
}
