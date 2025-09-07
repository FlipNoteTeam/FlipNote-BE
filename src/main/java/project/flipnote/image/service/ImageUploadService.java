package project.flipnote.image.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.image.entity.Image;
import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.entity.ImageStatus;
import project.flipnote.image.exception.ImageErrorCode;
import project.flipnote.image.model.ImageUploadResponseDto;
import project.flipnote.image.repository.ImageRepository;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserProfileRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadService {

	@Value("${cloud.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region}")
	private String region;

	private final ImageRefService imageRefService;
	private final ImageRepository imageRepository;
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;
	private static final int EXPIRE_MINUTES = 5;

	//확장자 형식 찾기
	private String getContentType(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

		return switch (extension) {
			case "jpg", "jpeg" -> "image/jpeg";
			case "png" -> "image/png";
			case "gif" -> "image/gif";
			case "webp" -> "image/webp";
			default -> "application/octet-stream";
		};
	}

	// presigned URL 생성
	@Transactional
	public ImageUploadResponseDto getPresignedUrl(String fileName) {

		String hash = fileName.split("\\.")[0];

		log.info(hash);

		// DB에 동일한 파일명이 이미 존재하는지 확인
		Optional<Image> existImage = imageRepository.findByHash(hash);
		if(existImage.isPresent()) {
			ImageRef imageRef = ImageRef.builder()
				.image(existImage.get())
				.build();

			imageRefService.save(imageRef);

			String url = generateUrl(existImage.get().getS3Key());

			return ImageUploadResponseDto.from(url, imageRef.getId());
		}

		// PutObjectRequest 정의
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(fileName)
			.contentType(getContentType(fileName))
			.build();

		// Presigned 요청 생성 (5분 유효)
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(EXPIRE_MINUTES))
			.putObjectRequest(putObjectRequest)
			.build();

		URL presignedUrl = s3Presigner.presignPutObject(presignRequest).url();

		String saveUrl = presignedUrl.toString().split("\\?")[0];

		Image image = Image.builder()
			.hash(hash)
			.s3Key(fileName)
			.build();

		imageRepository.save(image);

		ImageRef imageRef = ImageRef.builder()
			.image(image)
			.build();

		imageRefService.save(imageRef);

		String url = generateUrl(hash);

		return ImageUploadResponseDto.from(url, imageRef.getId());
	}

	public void changeUrlStatus(String key, ImageStatus status) {
		Image image = imageRepository.findByHash(key).orElseThrow(
			() -> new BizException(ImageErrorCode.IMAGE_NOT_FOUND)
		);

		// image.changeStatus(status);
	}

	public String generateUrl(String key) {
		return  "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
	}

	// 파일 존재 여부 확인
	private boolean objectExists(String fileName) {
		try {
			s3Client.headObject(
				HeadObjectRequest.builder()
					.bucket(bucket)
					.key(fileName)
					.build()
			);
			return true;
		} catch (S3Exception e) {
			// 404면 존재하지 않음
			if (e.statusCode() == 404) {
				return false;
			}
			throw new BizException(ImageErrorCode.S3_SERVICE_ERROR);
		}
	}
}
