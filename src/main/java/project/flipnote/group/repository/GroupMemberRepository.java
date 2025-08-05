package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.group.entity.Group;

import org.springframework.stereotype.Repository;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.user.entity.UserProfile;

import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupAndUser(Group group, UserProfile userProfile);

	long countByGroup_Id(Long groupId);

}
