package project.flipnote.groupapplication.model;

public record GroupApplicationRespondResponse(
        Long groupApplicationId
) {
    public static GroupApplicationRespondResponse from(Long groupApplicationId) {
        return new GroupApplicationRespondResponse(groupApplicationId);
    }
}
