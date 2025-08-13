package project.flipnote.group.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.entity.BaseEntity;
import project.flipnote.common.exception.BizException;
import project.flipnote.group.exception.GroupErrorCode;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "app_groups")
@Entity
public class Group extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Category category;

	@Column(nullable = false)
	private String description;

	private Boolean applicationRequired;

	@Column(name = "is_public", nullable = false)
	private Boolean publicVisible;

	@Column(nullable = false)
	@Min(1)
	@Max(100)
	private Integer maxMember;

	private String imageUrl;

	@Column(nullable = false)
	@Min(1)
	@Max(100)
	private Integer memberCount;

	@Builder
	private Group(
		String name,
		Category category,
		String description,
		Boolean applicationRequired,
		Boolean publicVisible,
		Integer maxMember,
		String imageUrl
	) {
		this.name = name;
		this.category = category;
		this.description = description;
		this.applicationRequired = applicationRequired;
		this.publicVisible = publicVisible;
		this.maxMember = maxMember;
		this.imageUrl = imageUrl;
		this.memberCount = 0;
	}

	public void validateJoinable() {
		if (memberCount >= maxMember) {
			throw new BizException(GroupErrorCode.GROUP_IS_ALREADY_MAX_MEMBER);
		}
	}

	public void increaseMemberCount() {
		this.memberCount++;
	}
}
