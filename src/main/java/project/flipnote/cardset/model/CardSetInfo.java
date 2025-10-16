package project.flipnote.cardset.model;

import project.flipnote.cardset.entity.CardSet;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;

public record CardSetInfo(
	CardSet cardSet,
	Group group,
	String name,
	Category category,
	String hashtag,
	String imageUrl,
	Long imageRefId
) {
}
