package project.flipnote.group.model;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
		@Min(1)
		@Max(100)
		Integer maxMember,

		@URL
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
