package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.LockModeType;
import project.flipnote.group.entity.Group;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.user.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupAndUser(Group group, User user);

	long countByGroup_Id(Long groupId);

}
