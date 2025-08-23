package project.flipnote.group.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.user.entity.UserProfile;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long>, GroupMemberRepositoryCustom {
	Optional<GroupMember> findByGroupAndUser(Group group, UserProfile userProfile);

	long countByGroup_Id(Long groupId);

	boolean existsByGroup_idAndUser_id(Long groupId, Long userId);

	Optional<GroupMember> findByGroup_IdAndUser_Id(Long groupId, Long userId);

	long countByGroup_idAndUser_idNot(Long groupId, Long userId);

	List<GroupMember> findByGroupAndRoleIn(Group group, List<GroupMemberRole> roles);

	boolean existsByGroup_IdAndUser_Id(Long groupId, Long id);
}
