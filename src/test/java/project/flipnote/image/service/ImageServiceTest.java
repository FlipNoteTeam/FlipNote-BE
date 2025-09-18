package project.flipnote.image.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.repository.UserProfileRepository;
import software.amazon.awssdk.services.s3.S3Client;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {
	@InjectMocks
	ImageService imageService;
	
	@Mock
	UserProfileRepository userRepository;

	@Mock
	private S3Client s3Client;

	UserProfile user;
	AuthPrinciple authPrinciple;

	private final String bucket = "your-bucket-name";
	private final String fileName = "test-image.jpg";

	@BeforeEach
	void before() {
		ReflectionTestUtils.setField(imageService, "bucket", bucket);
	}
	
	// @Test
	// public void 이미지_업로드_성공() throws Exception {
	// 	// given
	// 	when(amazonS3.doesObjectExist(bucket, fileName)).thenReturn(false);
	// 	URL fakeUrl = mock(URL.class);
	// 	when(amazonS3.generatePresignedUrl(any(GeneratePresignedUrlRequest.class))).thenReturn(fakeUrl);
	//
	// 	// when
	// 	ImageUploadResponseDto result = imageUploadService.getPresignedUrl(authPrinciple, fileName);
	//
	// 	// then
	// 	assertEquals(fakeUrl, result.url()); // DTO 내부에서 URL.toString() 했다고 가정
	// }
	//
	// @Test
	// public void 이미지_업로드_실패_중복() throws Exception {
	// 	// given
	// 	when(amazonS3.doesObjectExist(bucket, fileName)).thenReturn(true);
	//
	// 	// when & then
	// 	BizException exception = assertThrows(BizException.class, () -> {
	// 		imageUploadService.getPresignedUrl(authPrinciple, fileName);
	// 	});
	//
	// 	assertEquals(ImageErrorCode.CONFLICT_IMAGE, exception.getErrorCode());
	// 	verify(amazonS3, never()).generatePresignedUrl(any(GeneratePresignedUrlRequest.class));
	// }
}
