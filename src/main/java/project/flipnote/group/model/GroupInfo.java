package project.flipnote.group.model;

import project.flipnote.group.entity.Category;

public record GroupInfo(
	Long groupId,
	String name,
	String description,
	Category category,
	String imageUrl,
	Long imageRefId) {
	public static GroupInfo from(Long groupId, String name, String description, Category category, String imageUrl, Long imageRefId) {
		return new GroupInfo(groupId, name, description, category, imageUrl, imageRefId);
	}
}
