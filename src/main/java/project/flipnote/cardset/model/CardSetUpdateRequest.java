package project.flipnote.cardset.model;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import project.flipnote.group.entity.Category;

public record CardSetUpdateRequest(

	@NotBlank
	@Size(max = 50)
	String name,

	@NotNull
	Boolean publicVisible,

	@NotNull
	Category category,

	@NotNull
	List<String> hashtag,

	@URL
	String image
) {

	@Schema(hidden = true)
	public String getHashTag() {
		return hashtag != null && !hashtag.isEmpty() ? String.join(",", hashtag) : null;
	}
}
