package project.flipnote.groupjoin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum GroupJoinErrorCode implements ErrorCode {
    USER_NOT_IN_GROUP(HttpStatus.NOT_FOUND, "GROUP_JOIN_001", "그룹에 유저가 존재하지 않습니다."),
    USER_NOT_PERMISSION(HttpStatus.FORBIDDEN, "GROUP_JOIN_002", "그룹 내 권한이 없습니다."),
    GROUP_IS_NOT_PUBLIC(HttpStatus.FORBIDDEN, "GROUP_JOIN_003", "그룹이 비공개입니다."),
    NOT_EXIST_JOIN(HttpStatus.NOT_FOUND, "GROUP_JOIN_003", "가입 신청이 존재하지 않습니다."),
    ALREADY_JOINED_GROUP(HttpStatus.CONFLICT, "GROUP_JOIN_004", "이미 신청한 그룹입니다."),
    GROUP_IS_ALREADY_MAX_MEMBER(HttpStatus.CONFLICT, "GROUP_JOIN_005", "그룹 정원이 가득 찼습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public int getStatus() {
        return httpStatus.value();
    }
}
