package project.flipnote.common.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import project.flipnote.common.exception.ErrorCode;

@Getter
@AllArgsConstructor
public class CustomSecurityException extends RuntimeException {

	private ErrorCode errorCode;
}
