package project.flipnote.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

	@Column(nullable = false, length = 50, unique = true)
	private String name;

	@Builder
	private GroupPermission(String name) {
		this.name = name;
	}
}
