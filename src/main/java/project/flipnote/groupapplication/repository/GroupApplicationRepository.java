package project.flipnote.groupapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.groupapplication.entity.GroupApplication;

@Repository
public interface GroupApplicationRepository extends JpaRepository<GroupApplication, Long> {
}
