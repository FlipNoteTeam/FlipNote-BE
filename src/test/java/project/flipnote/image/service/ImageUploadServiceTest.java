package project.flipnote.image.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.net.URL;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.fixture.UserFixture;
import project.flipnote.image.exception.ImageErrorCode;
import project.flipnote.image.model.ImageUploadResponseDto;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ImageUploadServiceTest {
	@InjectMocks
	ImageUploadService imageUploadService;
	
	@Mock
	UserRepository userRepository;

	@Mock
	private AmazonS3 amazonS3;

	User user;
	AuthPrinciple authPrinciple;

	private final String bucket = "your-bucket-name";
	private final String fileName = "test-image.jpg";

	@BeforeEach
	void before() {
		ReflectionTestUtils.setField(imageUploadService, "bucket", bucket);
		user = UserFixture.createActiveUser();
		authPrinciple = new AuthPrinciple(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());

		// 사용자 검증 로직
		given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
	}
	
	@Test
	public void 이미지_업로드_성공() throws Exception {
		// given
		when(amazonS3.doesObjectExist(bucket, fileName)).thenReturn(false);
		URL fakeUrl = mock(URL.class);
		when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(fakeUrl);

		// when
		ImageUploadResponseDto result = imageUploadService.getPresignedUrl(authPrinciple, fileName);

		// then
		assertEquals(fakeUrl, result.url()); // DTO 내부에서 URL.toString() 했다고 가정
	}
	
	@Test
	public void 이미지_업로드_실패_중복() throws Exception {
		// given
		when(amazonS3.doesObjectExist(bucket, fileName)).thenReturn(true);

		// when & then
		BizException exception = assertThrows(BizException.class, () -> {
			imageUploadService.getPresignedUrl(authPrinciple, fileName);
		});

		assertEquals(ImageErrorCode.CONFLICT_IMAGE, exception.getErrorCode());
		verify(amazonS3, never()).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
	}
}
