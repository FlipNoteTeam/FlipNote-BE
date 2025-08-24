package project.flipnote.group.model;

public record GroupInfo(
	Long groupId,
	String name,
	String description
) {
	public static GroupInfo from(Long groupId, String name, String description) {
		return new GroupInfo(groupId, name, description);
	}
}
