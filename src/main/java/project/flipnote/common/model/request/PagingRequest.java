package project.flipnote.common.model.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingRequest {

	@Min(1)
	private Integer page = 1;

	@Min(1)
	@Max(30)
	private Integer size = 10;

	private String sortBy;

	private String order = "desc";

	@Schema(hidden = true)
	public PageRequest getPageRequest() {
		if (sortBy == null || sortBy.isEmpty()) {
			return PageRequest.of(page - 1, size + 1);
		} else {
			Sort.Direction direction;
			try {
				direction = Sort.Direction.fromString(order);
			} catch (IllegalArgumentException e) {
				direction = Sort.Direction.DESC;
			}

			return PageRequest.of(page - 1, size + 1, Sort.by(direction, sortBy));
		}
	}
}
