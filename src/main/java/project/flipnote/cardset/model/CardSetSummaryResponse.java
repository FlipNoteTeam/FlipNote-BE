package project.flipnote.cardset.model;

import project.flipnote.cardset.entity.CardSet;

public record CardSetSummaryResponse(
	Long cardSetId,
	Long groupId,
	String name,
	String category,
	String hashtag,
	String imageUrl
) {

	public static CardSetSummaryResponse from(CardSet cardSet) {
		return new CardSetSummaryResponse(
			cardSet.getId(),
			cardSet.getGroup().getId(),
			cardSet.getName(),
			cardSet.getCategory().name(),
			cardSet.getHashtag(),
			cardSet.getImageUrl()
		);
	}
}
