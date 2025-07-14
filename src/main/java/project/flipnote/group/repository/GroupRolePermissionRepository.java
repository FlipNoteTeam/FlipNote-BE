package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.group.entity.GroupRolePermission;

public interface GroupRolePermissionRepository extends JpaRepository<GroupRolePermission, Long> {
}
