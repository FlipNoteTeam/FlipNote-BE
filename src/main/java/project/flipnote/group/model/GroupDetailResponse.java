package project.flipnote.group.model;

import java.time.LocalDateTime;

import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;

public record GroupDetailResponse(

	String name,

	Category category,

	String description,

	Boolean applicationRequired,

	Boolean publicVisible,

	Integer maxMember,

	String imageUrl,

	LocalDateTime createdAt,

	LocalDateTime modifiedAt
) {
	public static GroupDetailResponse from(Group group) {
		return new GroupDetailResponse(
			group.getName(),
			group.getCategory(),
			group.getDescription(),
			group.getApplicationRequired(),
			group.getPublicVisible(),
			group.getMaxMember(),
			group.getImageUrl(),
			group.getCreatedAt(),
			group.getModifiedAt()
		);
	}
}
