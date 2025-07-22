package project.flipnote.groupapplication.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.entity.BaseEntity;
import project.flipnote.group.entity.Group;
import project.flipnote.user.entity.User;

@Getter
@Entity
@Table(name = "group_applications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupApplication extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, columnDefinition = "PENDING")
	private GroupApplicationStatus status;

	private String joinIntro;

	@Builder
	public GroupApplication
		(
			User user,
			Group group,
			GroupApplicationStatus status,
			String joinIntro
		)

	{
		this.user = user;
		this.group = group;
		this.status = status;
		this.joinIntro = joinIntro;
	}

	public void updateStatus(GroupApplicationStatus status) {
		this.status = status;
	}
}
