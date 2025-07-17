package project.flipnote.group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.entity.GroupPermission;
import project.flipnote.group.entity.GroupRolePermission;

public interface GroupRolePermissionRepository extends JpaRepository<GroupRolePermission, Long> {
    boolean existByGroupAndRoleAndGroupPermission(Group group, GroupMemberRole role, GroupPermission groupPermission);
}
