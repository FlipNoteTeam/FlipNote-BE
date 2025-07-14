package project.flipnote.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
/*
각 그룹 내에서 역할별로 가지는 권한들을 관리하는 엔티티
 */
@Entity
@Table(name = "group_role_permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupRolePermission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@ManyToOne
	@JoinColumn(name = "group_permission_id", nullable = false)
	private GroupPermission groupPermission;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private GroupRole role;

	@Builder
	private GroupRolePermission(Group group, GroupPermission groupPermission, GroupRole role) {
		this.group = group;
		this.groupPermission = groupPermission;
		this.role = role;
	}
}
