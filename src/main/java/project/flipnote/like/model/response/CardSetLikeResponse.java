package project.flipnote.like.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import project.flipnote.cardset.model.CardSetSummaryResponse;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class CardSetLikeResponse extends LikeTargetResponse {
	private Long id;
	private String name;

	public static CardSetLikeResponse from(CardSetSummaryResponse res) {
		return new CardSetLikeResponse(res.cardSetId(), res.name());
	}
}
