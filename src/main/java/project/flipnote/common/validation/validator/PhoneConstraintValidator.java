package project.flipnote.common.validation.validator;

import java.util.Objects;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import project.flipnote.common.validation.annotation.ValidPhone;

public class PhoneConstraintValidator implements ConstraintValidator<ValidPhone, String> {

	private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

	@Override
	public boolean isValid(String phone, ConstraintValidatorContext context) {
		if (Objects.isNull(phone)) {
			return true;
		}

		try {
			Phonenumber.PhoneNumber number = phoneUtil.parse(phone, "KR");
			return phoneUtil.isValidNumber(number);
		} catch (NumberParseException e) {
			return false;
		}
	}
}
