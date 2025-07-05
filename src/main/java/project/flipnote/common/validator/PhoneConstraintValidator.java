package project.flipnote.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import project.flipnote.common.annotation.ValidPhone;

public class PhoneConstraintValidator implements ConstraintValidator<ValidPhone, String> {

	private static final String PHONE_PATTERN = "^010-\\d{4}-\\d{4}$";

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (value == null || value.isBlank()) {
			return false;
		}
		return value.matches(PHONE_PATTERN);
	}
}
