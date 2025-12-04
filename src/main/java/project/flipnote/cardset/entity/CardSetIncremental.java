package project.flipnote.cardset.entity;

import org.hibernate.annotations.ColumnDefault;

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
public class CardSetIncremental extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cardset_id", nullable = false)
	private Long cardSetId;

	@Lob
	@Column(name = "incremental_value", nullable = false)
	private byte[] incrementalValue;

	@Column(name = "is_flushed")
	private boolean flushed;

	@Builder
	private CardSetIncremental(Long cardSetId, byte[] incrementalValue, boolean flushed) {
		this.cardSetId = cardSetId;
		this.incrementalValue = incrementalValue;
		this.flushed = flushed;
	}
}
