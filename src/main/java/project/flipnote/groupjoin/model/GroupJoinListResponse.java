package project.flipnote.groupjoin.model;

import project.flipnote.groupjoin.entity.GroupJoin;

import java.util.List;

public record GroupJoinListResponse(
        List<GroupJoin> groupJoins
) {
    public static GroupJoinListResponse from(List<GroupJoin> groupJoins) {
        return new GroupJoinListResponse(groupJoins);
    }
}
