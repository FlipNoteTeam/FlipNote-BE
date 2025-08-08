package project.flipnote.cardset.model;


public record CreateCardSetResponse(
	Long cardSetId
) {
	public static CreateCardSetResponse from(Long cardSetId) {
		return new CreateCardSetResponse(cardSetId);
	}
}
