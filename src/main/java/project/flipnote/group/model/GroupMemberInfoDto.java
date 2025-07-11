package project.flipnote.group.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import project.flipnote.group.entity.Category;

public class GroupMemberInfoDto {

	public record Response(
		Long groupId
	){
		public static Response from(Long groupId) {
			return new Response(groupId);
		}
	}
}
