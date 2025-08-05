package project.flipnote.groupjoin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
import project.flipnote.group.entity.Group;
import project.flipnote.user.entity.UserProfile;

@Getter
@Entity
@Table(name = "group_joins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupJoin extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserProfile user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GroupJoinStatus status;

	private String joinIntro;

	@Builder
	public GroupJoin
		(
			UserProfile user,
			Group group,
			GroupJoinStatus status,
			String joinIntro
		)

	{
		this.user = user;
		this.group = group;
		this.status = status;
		this.joinIntro = joinIntro;
	}

	public void updateStatus(GroupJoinStatus status) {
		this.status = status;
	}
}
