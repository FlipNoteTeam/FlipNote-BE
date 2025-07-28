package project.flipnote.groupjoin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.group.entity.Group;
import project.flipnote.groupjoin.entity.GroupJoin;
import project.flipnote.user.entity.User;

import java.util.List;

@Repository
public interface GroupJoinRepository extends JpaRepository<GroupJoin, Long> {
    List<GroupJoin> findAllByGroup(Group group);

    List<GroupJoin> findAllByUser(User user);
}
