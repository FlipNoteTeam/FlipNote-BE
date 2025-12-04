package project.flipnote.cardset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.flipnote.common.entity.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cardset_contents")
@Entity
public class CardSetContent extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cardset_id", nullable = false)
	private Long cardSetId;

	@Lob
	@Column(nullable = false)
	private String content;

	@Builder
	private CardSetContent(Long cardSetId, String content) {
		this.cardSetId = cardSetId;
		this.content = content;
	}
}
