package project.flipnote.groupjoin.model;

public record GroupJoinRespondResponse(
        Long groupJoinId
) {
    public static GroupJoinRespondResponse from(Long groupJoinId) {
        return new GroupJoinRespondResponse(groupJoinId);
    }
}
