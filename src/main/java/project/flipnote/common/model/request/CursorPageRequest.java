package project.flipnote.common.model.request;

import org.springframework.util.StringUtils;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CursorPageRequest {

	private String cursor;

	@Min(1)
	@Max(30)
	private Integer size = 10;

	@Schema(hidden = true)
	public Long getCursorId() {
		if (!StringUtils.hasText(cursor)) {
			return null;
		}

		final String normalized = cursor.trim();
		if (normalized.isEmpty()) {
			return null;
		}

		return Long.valueOf(normalized);
	}
}
