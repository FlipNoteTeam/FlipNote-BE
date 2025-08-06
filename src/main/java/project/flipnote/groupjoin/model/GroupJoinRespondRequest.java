package project.flipnote.groupjoin.model;

import project.flipnote.groupjoin.entity.GroupJoinStatus;

public record GroupJoinRespondRequest(
        GroupJoinStatus status
) {
}
