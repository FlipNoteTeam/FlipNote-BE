package project.flipnote.group.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum GroupErrorCode implements ErrorCode {
	GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_001", "그룹이 존재하지 않습니다."),
	INVALID_MAX_MEMBER(HttpStatus.BAD_REQUEST, "GROUP_002", "최대 인원 수는 1 이상 100 이하여야 합니다."),
	USER_NOT_PERMISSION(HttpStatus.FORBIDDEN, "GROUP_003", "그룹 내 권한이 없습니다."),
	USER_NOT_IN_GROUP(HttpStatus.NOT_FOUND, "GROUP_004", "그룹에 유저가 존재하지 않습니다."),
	OTHER_USER_EXIST_IN_GROUP(HttpStatus.CONFLICT, "GROUP_005", "그룹내 오너 제외 유저가 존재합니다."),
	GROUP_IS_ALREADY_MAX_MEMBER(HttpStatus.CONFLICT, "GROUP_006", "그룹 정원이 가득 찼습니다."),
	ALREADY_GROUP_MEMBER(HttpStatus.CONFLICT, "GROUP_007", "이미 그룹 회원입니다."),
	INVALID_MEMBER_COUNT(HttpStatus.BAD_REQUEST, "GROUP_008", "그룹 내에 인원수보다 많게 수정해야합니다."),
	INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "GROUP_009", "지원하지 않는 카테고리입니다." );

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
