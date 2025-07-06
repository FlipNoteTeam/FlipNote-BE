package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.group.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
