package project.flipnote.groupapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.group.entity.Group;
import project.flipnote.groupapplication.entity.GroupApplication;

import java.util.List;

@Repository
public interface GroupApplicationRepository extends JpaRepository<GroupApplication, Long> {
    List<GroupApplication> findAllByGroup(Group group);

}
