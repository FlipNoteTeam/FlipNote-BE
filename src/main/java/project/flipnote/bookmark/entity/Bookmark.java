package project.flipnote.bookmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
	name = "bookmarks",
	indexes = {
		@Index(
			name = "idx_bookmarks_targettype_userid_targetid",
			columnList = "target_type, user_id, target_id"
		)
	},
	uniqueConstraints = {
		@UniqueConstraint(
			name = "uk_bookmarks_targettype_userid_targetid",
			columnNames = {"target_type", "user_id", "target_id"}
		)
	}
)
@Entity
public class Bookmark extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookmarkTargetType targetType;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false)
	private Long userId;

	@Builder
	public Bookmark(BookmarkTargetType targetType, Long targetId, Long userId) {
		this.targetType = targetType;
		this.targetId = targetId;
		this.userId = userId;
	}
}
