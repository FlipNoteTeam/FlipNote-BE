package project.flipnote.cardset.model;

import project.flipnote.group.entity.Category;

public record CardSetUpdatePayload(
	String name,
	Boolean publicVisible,
	Category category,
	String hashtag,
	Long imageRefId
) {

	public static CardSetUpdatePayload from(CardSetUpdateRequest req) {
		return new CardSetUpdatePayload(
			req.name(),
			req.publicVisible(),
			req.category(),
			req.getHashTag(),
			req.imageRefId()
		);
	}
}
