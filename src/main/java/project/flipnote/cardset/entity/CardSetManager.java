package project.flipnote.cardset.entity;

import jakarta.persistence.Entity;
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
import project.flipnote.user.entity.UserProfile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "card_set_managers",
	uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "card_set_id"}),
	indexes = {
		@Index(name = "idx_card_set_manager_user", columnList = "user_id"),
		@Index(name = "idx_card_set_manager_cardset", columnList = "card_set_id")
	})
@Entity
public class CardSetManager {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserProfile user;

	@ManyToOne
	@JoinColumn(name = "card_set_id", nullable = false)
	private CardSet cardSet;

	@Builder
	private CardSetManager(UserProfile user, CardSet cardSet) {
		this.user = user;
		this.cardSet = cardSet;
	}
}
