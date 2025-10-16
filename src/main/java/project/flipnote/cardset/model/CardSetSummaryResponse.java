package project.flipnote.cardset.model;

import project.flipnote.cardset.entity.CardSet;

public record CardSetSummaryResponse(
	Long cardSetId,
	Long groupId,
	String name,
	String category,
	String hashtag,
	String imageUrl,
	Long imageRefId
) {

	public static CardSetSummaryResponse from(CardSetInfo cardSetInfo) {
		return new CardSetSummaryResponse(
			cardSetInfo.cardSet().getId(),
			cardSetInfo.group().getId(),
			cardSetInfo.name(),
			cardSetInfo.category().name(),
			cardSetInfo.hashtag(),
			cardSetInfo.imageUrl(),
			cardSetInfo.imageRefId()
		);
	}
}
