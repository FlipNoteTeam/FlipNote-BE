package project.flipnote.group.model;

public class GroupInfoDto {
	public record Response(
		Long groupId
	) {
		public static Response from(Long groupId) {
			return new Response(groupId);
		}
	}
}
