package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.group.entity.GroupInvitation;

public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {

	boolean existsByGroupIdAndInviteeUserId(Long groupId, Long inviteeUserId);

	boolean existsByGroupIdAndInviteeEmail(Long groupId, String inviteeEmail);
}
