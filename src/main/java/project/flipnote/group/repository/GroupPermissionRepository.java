package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.group.entity.GroupPermission;

@Repository
public interface GroupPermissionRepository extends JpaRepository<GroupPermission, Long> {
}
