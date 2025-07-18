package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;
import project.flipnote.group.entity.GroupRolePermission;

@Repository
public interface GroupRolePermissionRepository extends JpaRepository<GroupRolePermission, Long> {
}
