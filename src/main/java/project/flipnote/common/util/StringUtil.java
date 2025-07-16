package project.flipnote.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtil {

	public static String cleanPhone(String phone) {
		return phone == null ? null : phone.replaceAll("-", "");
	}
}
