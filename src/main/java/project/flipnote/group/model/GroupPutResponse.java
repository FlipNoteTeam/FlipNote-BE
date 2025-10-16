package project.flipnote.group.model;

import java.time.LocalDateTime;

import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;

public record GroupPutResponse(
	String name,

	Category category,

	String description,

	Boolean applicationRequired,

	Boolean publicVisible,

	Integer maxMember,

	String imageUrl,

	Long imageRefId,

	LocalDateTime createdAt,

	LocalDateTime modifiedAt
) {
	public static GroupPutResponse from(Group group, Long imageRefId) {
		return new GroupPutResponse(
			group.getName(),
			group.getCategory(),
			group.getDescription(),
			group.getApplicationRequired(),
			group.getPublicVisible(),
			group.getMaxMember(),
			group.getImageUrl(),
			imageRefId,
			group.getCreatedAt(),
			group.getModifiedAt()
		);
	}
}
