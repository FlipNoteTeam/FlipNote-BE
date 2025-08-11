package project.flipnote.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "group_invitations")
public class GroupInvitation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long groupId;

	@Column(nullable = false)
	private Long inviterUserId;

	private Long inviteeUserId;

	private String inviteeEmail;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GroupInvitationStatus status;

	@Builder
	public GroupInvitation(Long groupId, Long inviterUserId, Long inviteeUserId, String inviteeEmail) {
		this.groupId = groupId;
		this.inviterUserId = inviterUserId;
		this.inviteeUserId = inviteeUserId;
		this.inviteeEmail = inviteeEmail;
		this.status = GroupInvitationStatus.PENDING;
	}

	public void respond(GroupInvitationStatus status) {
		this.status = status;
	}
}
