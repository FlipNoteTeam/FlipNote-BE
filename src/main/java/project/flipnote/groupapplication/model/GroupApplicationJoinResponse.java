package project.flipnote.groupapplication.model;

public record GroupApplicationJoinResponse(
		Long groupApplicationId
) {
	public static GroupApplicationJoinResponse from(Long groupApplicationId) {
		return new GroupApplicationJoinResponse(groupApplicationId);
	}
}
