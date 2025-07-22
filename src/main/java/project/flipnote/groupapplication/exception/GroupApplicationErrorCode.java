package project.flipnote.groupapplication.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum GroupApplicationErrorCode implements ErrorCode {
    USER_NOT_IN_GROUP(HttpStatus.NOT_FOUND, "GROUP_APPLICATION_001", "그룹에 유저가 존재하지 않습니다."),
    USER_NOT_PERMISSION(HttpStatus.FORBIDDEN, "GROUP_APPLICATION_002", "그룹 내 권한이 없습니다."),
    NOT_EXIST_JOIN(HttpStatus.NOT_FOUND, "GROUP_APPLICATION_003", "가입 신청이 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public int getStatus() {
        return httpStatus.value();
    }
}
