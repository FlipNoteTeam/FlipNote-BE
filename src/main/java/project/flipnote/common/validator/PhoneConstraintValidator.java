package project.flipnote.common.validator;

import java.util.Objects;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import project.flipnote.common.annotation.ValidPhone;

public class PhoneConstraintValidator implements ConstraintValidator<ValidPhone, String> {

	private static final String PHONE_PATTERN = "^010-\\d{4}-\\d{4}$";

	@Override
	public boolean isValid(String phone, ConstraintValidatorContext context) {
		if (Objects.isNull(phone)) {
			return true;
		}

		return phone.matches(PHONE_PATTERN);
	}
}
