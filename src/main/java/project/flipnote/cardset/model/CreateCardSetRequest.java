package project.flipnote.cardset.model;

import java.util.List;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import project.flipnote.group.entity.Category;

public record CreateCardSetRequest(

	@NotBlank
	@Size(max = 50)
	String name,

	@NotNull
	Boolean publicVisible,

	@NotNull
	Category category,

	@NotNull
	List<String> hashtag,

	@NotEmpty @Size(min = 1)
	Set<Long> managers,

	Long imageRefId
) {
}
