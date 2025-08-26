package project.flipnote.notification.model;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import project.flipnote.common.model.request.CursorPagingRequest;

@Getter
@Setter
public class NotificationListRequest extends CursorPagingRequest {

	@Min(1)
	private Long groupId;

	private Boolean read;

	@Override
	public PageRequest getPageRequest() {
		return PageRequest.of(0, getSize(), Sort.by(Sort.Direction.DESC, "id"));
	}
}
