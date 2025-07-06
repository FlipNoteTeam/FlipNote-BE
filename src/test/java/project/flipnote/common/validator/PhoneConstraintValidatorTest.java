package project.flipnote.common.validator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.validation.ConstraintValidatorContext;

@DisplayName("휴대폰 번호 유효성 검증기 단위 테스트")
@ExtendWith(MockitoExtension.class)
class PhoneConstraintValidatorTest {

	PhoneConstraintValidator validator = new PhoneConstraintValidator();

	@Mock
	ConstraintValidatorContext context;

	@Test
	@DisplayName("010-1234-5678 형식은 true를 반환한다")
	void validPhoneWithHyphens() {
		assertThat(validator.isValid("010-1234-5678", context)).isTrue();
		assertThat(validator.isValid("010-4567-1238", context)).isTrue();
	}

	@Test
	@DisplayName("null은 true를 반환한다")
	void validPhoneNull() {
		assertThat(validator.isValid(null, context)).isTrue();
	}

	@Test
	@DisplayName("01012345678 형식은 false를 반환한다")
	void validPhoneWithoutHyphens() {
		assertThat(validator.isValid("01012345678", context)).isFalse();
	}

	@Test
	@DisplayName("010-123-4567 등 잘못된 자리수는 false")
	void invalidPhoneWrongDigits() {
		assertThat(validator.isValid("010-123-4567", context)).isFalse();
		assertThat(validator.isValid("010-12345-6789", context)).isFalse();
		assertThat(validator.isValid("010-12345-678", context)).isFalse();
		assertThat(validator.isValid("010-12345-67890", context)).isFalse();
	}

	@Test
	@DisplayName("010이 아닌 번호는 false")
	void invalidPhoneNot010() {
		assertThat(validator.isValid("011-1234-5678", context)).isFalse();
		assertThat(validator.isValid("019-1234-5678", context)).isFalse();
	}

	@Test
	@DisplayName("숫자가 아닌 문자가 포함되면 false")
	void invalidPhoneWithLetters() {
		assertThat(validator.isValid("010-ABCD-5678", context)).isFalse();
		assertThat(validator.isValid("0101234abcd", context)).isFalse();
	}

	@Test
	@DisplayName("빈 문자열은 false")
	void invalidPhoneEmpty() {
		assertThat(validator.isValid("", context)).isFalse();
		assertThat(validator.isValid("   ", context)).isFalse();
	}
}
