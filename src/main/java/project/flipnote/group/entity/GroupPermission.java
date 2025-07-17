package project.flipnote.group.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
그룹 내 존재하는 역할들 엔티티
 */
@Entity
@Table(name = "group_permissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupPermission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50, unique = true)
	private GroupPermissionStatus name;

	@Builder
	private GroupPermission(GroupPermissionStatus name) {
		this.name = name;
	}
}
