package project.flipnote.groupjoin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.group.entity.Group;
import project.flipnote.groupjoin.entity.GroupJoin;
import project.flipnote.user.entity.UserProfile;

@Repository
public interface GroupJoinRepository extends JpaRepository<GroupJoin, Long>, GroupJoinRepositoryCustom {
	boolean existsByGroup_idAndUser_id(Long groupId, Long userId);
}
