package project.flipnote.cardset.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "card_set")
@Entity
public class CardSet extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "group_id", nullable = false)
	private Group group;

	@Column(name = "is_public", nullable = false)
	private Boolean publicVisible;

	@Column(nullable = false)
	private Category category;

	private String hashtag;

	@Column(nullable = false)
	private String imageUrl;

	@Column
	private LocalDateTime deletedAt;

	@Builder
	private CardSet(String name, Group group, Boolean publicVisible, Category category, String hashtag,
		String imageUrl) {
		this.name = name;
		this.group = group;
		this.publicVisible = publicVisible;
		this.category = category;
		this.hashtag = hashtag;
		this.imageUrl = imageUrl;
	}

	public void deleteCardSet() {
		this.deletedAt = LocalDateTime.now();
	}
}
