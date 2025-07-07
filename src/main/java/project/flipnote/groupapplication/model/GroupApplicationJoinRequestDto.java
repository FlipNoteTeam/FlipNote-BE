package project.flipnote.groupapplication.model;

public class GroupApplicationJoinRequestDto {
	public record Request(
		String joinIntro
	){}

	public record Response(
		Long groupApplicationId
	){
		public static GroupApplicationJoinRequestDto.Response from(Long groupApplicationId) {
			return new GroupApplicationJoinRequestDto.Response(groupApplicationId);
		}
	}
}
