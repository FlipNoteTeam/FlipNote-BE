package project.flipnote.groupjoin.model;

import jakarta.validation.constraints.NotNull;
import project.flipnote.groupjoin.entity.GroupJoinStatus;

public record GroupJoinRespondRequest(
	@NotNull(message = "그룹 신청 상태를 선택해주세요.")
	GroupJoinStatus status
) {
}
