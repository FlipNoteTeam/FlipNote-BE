package project.flipnote.common.validator;

import java.util.Objects;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import project.flipnote.common.annotation.ValidPassword;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

	private static final String PASSWORD_PATTERN =
		"^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$^*()_\\-])[A-Za-z\\d!@#$^*()_\\-]{8,16}$";
	private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (Objects.isNull(password)) {
			return false;
		}

		return pattern.matcher(password).matches();
	}
}
