package project.flipnote.group.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import project.flipnote.group.entity.Category;

public class GroupCreateDto {
	public record Request(
		@NotBlank
		@Size(max = 50)
		String name,

		@NotNull
		Category category,

		@NotBlank
		String description,

		@NotNull
		Boolean applicationRequired,

		@NotNull
		Boolean publicVisible,

		@NotNull
		Integer maxMember,

		String image
	) {
	}

	public record Response(
		Long groupId
	){
		public static Response from(Long groupId) {
			return new Response(groupId);
		}
	}
}
