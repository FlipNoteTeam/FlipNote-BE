package project.flipnote.like.entity;

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
import project.flipnote.common.entity.LikeType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes")
@Entity
public class Like extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private LikeType type;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false)
	private Long userId;

	@Builder
	public Like(LikeType type, Long targetId, Long userId) {
		this.type = type;
		this.targetId = targetId;
		this.userId = userId;
	}
}
