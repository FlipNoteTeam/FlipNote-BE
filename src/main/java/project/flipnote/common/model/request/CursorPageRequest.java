package project.flipnote.common.model.request;

import org.springframework.util.StringUtils;

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

	public Long getCursorId() {
		return StringUtils.hasText(cursor) ? Long.valueOf(cursor) : null;
	}
}
