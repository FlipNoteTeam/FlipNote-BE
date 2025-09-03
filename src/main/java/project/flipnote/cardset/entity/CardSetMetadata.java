package project.flipnote.cardset.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "card_set_metadata")
@Entity
public class CardSetMetadata {

	@Id
	private Long id;

	@Column(nullable = false)
	private int likeCount;

	@Builder
	public CardSetMetadata(Long id) {
		this.id = id;
	}
}
