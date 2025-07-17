package project.flipnote.groupapplication.model;

import project.flipnote.groupapplication.entity.GroupApplication;

import java.util.List;

public record GroupApplicationListResponse(
        List<GroupApplication> groupApplications
) {
    public static GroupApplicationListResponse from(List<GroupApplication> groupApplications) {
        return new GroupApplicationListResponse(groupApplications);
    }
}
