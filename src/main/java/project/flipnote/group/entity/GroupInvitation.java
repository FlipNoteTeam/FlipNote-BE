package project.flipnote.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
	name = "group_invitation",
	indexes = {
		@Index(name = "idx_group_invitee_user", columnList = "group_id, invitee_user_id, status"),
		@Index(name = "idx_group_invitee_email", columnList = "group_id, invitee_email, status"),
		@Index(name = "idx_invitee_user_status", columnList = "invitee_user_id, status"),
		@Index(name = "idx_invitee_email_status", columnList = "invitee_email, status")
	},
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_group_invitee_user", columnNames = {"group_id", "invitee_user_id"}),
		@UniqueConstraint(name = "uq_group_invitee_email", columnNames = {"group_id", "invitee_email"})
	}
)
public class GroupInvitation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Column(nullable = false)
	private Long inviterUserId;

	private Long inviteeUserId;

	private String inviteeEmail;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GroupInvitationStatus status;

	@Builder
	public GroupInvitation(Group group, Long inviterUserId, Long inviteeUserId, String inviteeEmail) {
		this.group = group;
		this.inviterUserId = inviterUserId;
		this.inviteeUserId = inviteeUserId;
		this.inviteeEmail = inviteeEmail;
		this.status = GroupInvitationStatus.PENDING;
	}

	public void respond(GroupInvitationStatus status) {
		this.status = status;
	}
}
