package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.group.entity.GroupMemberInvitation;

public interface GroupMemberInvitationRepository extends JpaRepository<GroupMemberInvitation, Long> {

	boolean existsByGroupIdAndInviteeUserId(Long groupId, Long inviteeUserId);
}
