package project.flipnote.group.model;

public class GroupInfoDto {
	public record Response(
		Long groupId
	) {
		public static GroupCreateDto.Response from(Long groupId) {
			return new GroupCreateDto.Response(groupId);
		}
	}
}
