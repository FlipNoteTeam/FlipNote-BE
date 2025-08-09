package project.flipnote.group.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.ErrorCode;

@Getter
@RequiredArgsConstructor
public enum GroupInvitationErrorCode implements ErrorCode {
	ALREADY_INVITED(HttpStatus.CONFLICT, "GROUP_INVITATION_001", "이미 초대된 사용자입니다."),
	NO_INVITATION_PERMISSION(HttpStatus.FORBIDDEN, "INVITATION_002", "해당 그룹에 초대할 권한이 없습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public int getStatus() {
		return httpStatus.value();
	}
}
