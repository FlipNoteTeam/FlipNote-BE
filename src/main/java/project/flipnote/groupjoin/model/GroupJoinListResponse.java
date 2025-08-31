package project.flipnote.groupjoin.model;

import project.flipnote.groupjoin.entity.GroupJoin;

import java.util.List;

public record GroupJoinListResponse(
    List<GroupJoinInfo> groupJoins
) {
    public static GroupJoinListResponse from(List<GroupJoinInfo> groupJoins) {
        return new GroupJoinListResponse(groupJoins);
    }
}
