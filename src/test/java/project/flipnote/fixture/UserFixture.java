package project.flipnote.fixture;

import org.springframework.test.util.ReflectionTestUtils;

import project.flipnote.user.entity.UserProfile;

public class UserFixture {

	public static final String ENCODED_PASSWORD = "encodedPass";
	public static final String USER_EMAIL = "test@test.com";

	public static UserProfile createActiveUser() {
		UserProfile userProfile = UserProfile.builder()
			.email(USER_EMAIL)
			.password(ENCODED_PASSWORD)
			.nickname("테스트닉네임")
			.name("테스트이름")
			.phone("+821012345678")
			.smsAgree(true)
			.profileImageUrl("test_image_url")
			.build();

		ReflectionTestUtils.setField(userProfile, "id", 1L);

		return userProfile;
	}
}
