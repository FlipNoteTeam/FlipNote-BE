package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.group.entity.GroupGuestInvitation;

public interface GroupGuestInvitationRepository extends JpaRepository<GroupGuestInvitation, Long> {

	boolean existsByGroupIdAndInviteeEmail(Long groupId, String inviteeEmail);
}
