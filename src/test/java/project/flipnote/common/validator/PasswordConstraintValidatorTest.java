package project.flipnote.common.validator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;

@DisplayName("비밀번호 유효성 검증기 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PasswordConstraintValidatorTest {

	PasswordConstraintValidator validator = new PasswordConstraintValidator();

	@Mock
	ConstraintValidatorContext context;

	@Test
	@DisplayName("유효한 비밀번호는 true를 반환한다")
	void validPassword() {
		assertThat(validator.isValid("Abc12345!", context)).isTrue();
		assertThat(validator.isValid("A1b2c3d4@", context)).isTrue();
		assertThat(validator.isValid("aB1!abcd", context)).isTrue();
	}

	@Test
	@DisplayName("영문, 숫자, 특수문자 중 하나라도 빠지면 false")
	void invalidPassword_missingType() {
		assertThat(validator.isValid("abcdefgh", context)).isFalse();
		assertThat(validator.isValid("12345678!", context)).isFalse();
		assertThat(validator.isValid("Abcdefgh1", context)).isFalse();
	}

	@Test
	@DisplayName("허용되지 않은 특수문자 포함 시 false")
	void invalidPassword_wrongSpecialChar() {
		assertThat(validator.isValid("Abc12345%", context)).isFalse();
		assertThat(validator.isValid("Abc12345?", context)).isFalse();
	}

	@Test
	@DisplayName("길이 제한 위반 시 false")
	void invalidPassword_length() {
		assertThat(validator.isValid("A1!a", context)).isFalse();
		assertThat(validator.isValid("Abcdefghijk1!2345", context)).isFalse();
	}

	@Test
	@DisplayName("null 입력 시 false")
	void nullPassword() {
		assertThat(validator.isValid(null, context)).isFalse();
	}
}
