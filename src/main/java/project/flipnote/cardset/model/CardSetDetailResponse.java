package project.flipnote.cardset.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import project.flipnote.cardset.entity.CardSet;

public record CardSetDetailResponse(
	Long cardSetId,
	Long groupId,
	String name,
	String category,
	String hashtag,
	String imageUrl,
	boolean isPublicVisible,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime createdAt,

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime modifiedAt
) {

	public static CardSetDetailResponse from(CardSet cardSet) {
		return new CardSetDetailResponse(
			cardSet.getId(),
			cardSet.getGroup().getId(),
			cardSet.getName(),
			cardSet.getCategory().name(),
			cardSet.getHashtag(),
			cardSet.getImageUrl(),
			cardSet.getPublicVisible(),
			cardSet.getCreatedAt(),
			cardSet.getModifiedAt()
		);
	}
}
