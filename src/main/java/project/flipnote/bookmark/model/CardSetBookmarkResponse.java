package project.flipnote.bookmark.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import project.flipnote.cardset.model.CardSetSummaryResponse;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class CardSetBookmarkResponse extends BookmarkTargetResponse {
	private Long id;
	private String name;

	public static CardSetBookmarkResponse from(CardSetSummaryResponse res) {
		return new CardSetBookmarkResponse(res.cardSetId(), res.name());
	}
}
