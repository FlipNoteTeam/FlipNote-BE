package project.flipnote.group.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import project.flipnote.group.entity.Category;

public record GroupPutRequest(
	@NotBlank
	@Size(max = 50)
	String name,

	@NotNull
	Category category,

	@NotBlank
	@Size(max = 150)
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
