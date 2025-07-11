package project.flipnote.fixture;

import org.springframework.test.util.ReflectionTestUtils;

import project.flipnote.user.entity.User;

public class UserFixture {

	public static final String ENCODED_PASSWORD = "encodedPass";
	public static final String USER_EMAIL = "test@test.com";

	public static User createActiveUser() {
		User user = User.builder()
			.email(USER_EMAIL)
			.password(ENCODED_PASSWORD)
			.build();

		ReflectionTestUtils.setField(user, "id", 1L);

		return user;
	}
}
