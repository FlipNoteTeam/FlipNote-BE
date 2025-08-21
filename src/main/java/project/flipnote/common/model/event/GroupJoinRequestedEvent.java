package project.flipnote.common.model.event;

import java.util.List;

public record GroupJoinRequestedEvent(
	Long groupId,
	List<Long> receiverIds,
	Long requesterId
) {
}
