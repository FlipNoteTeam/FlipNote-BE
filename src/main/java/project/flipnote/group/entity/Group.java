package project.flipnote.group.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "groups")
@Entity
public class Group extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
	private Integer maxMember;

	private String imageUrl;

	//생성시 시간을 기준으로 저장
	@Column(nullable = false)
	@CreatedDate
	private LocalDateTime createdAt;

	//수정시 시간을 기준으로 저장
	@Column(nullable = false)
	@LastModifiedDate
	private LocalDateTime modifiedAt;

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
	}
}
