package project.flipnote.common.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PhoneUtil {

	private static final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

	public static String normalize(String phone) {
		if (phone == null) {
			return null;
		}

		try {
			Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phone, "KR");
			return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
		} catch (NumberParseException e) {
			throw new IllegalStateException("전화번호 정규화에 실패하였습니다. phone: " + phone, e);
		}
	}
}
