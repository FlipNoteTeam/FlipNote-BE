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
import project.flipnote.common.entity.BaseEntity;
import project.flipnote.user.entity.UserProfile;

@Getter
@Entity
@Table(name = "group_members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserProfile userProfile;

	//기본 값은 MEMBER;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GroupMemberRole role = GroupMemberRole.MEMBER;

	@Builder
	private GroupMember(Group group, UserProfile userProfile, GroupMemberRole role) {
		this.group = group;
		this.userProfile = userProfile;
		this.role = role != null ? role : GroupMemberRole.MEMBER;
	}
}
