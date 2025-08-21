package project.flipnote.notification.model;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import project.flipnote.common.model.request.CursorPageRequest;

@Getter
@Setter
public class NotificationListRequest extends CursorPageRequest {

	@Min(1)
	private Long groupId;

	private Boolean read;
}
