package project.flipnote.groupapplication.model;

import project.flipnote.groupapplication.entity.GroupApplicationStatus;

public record GroupApplicationRespondRequest(
        GroupApplicationStatus status
) {
}
