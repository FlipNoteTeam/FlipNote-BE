package project.flipnote.image.service;

import java.net.URL;
import java.time.Duration;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.image.exception.ImageErrorCode;
import project.flipnote.image.model.ImageUploadResponseDto;
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

@Service
@RequiredArgsConstructor
public class ImageUploadService {

	@Value("${cloud.s3.bucket}")
	private String bucket;

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;
	private final UserProfileRepository userRepository;
	private static final int EXPIRE_MINUTES = 5;

	//유저 찾기
	private void findUser(AuthPrinciple authPrinciple) {
		userRepository.findByIdAndStatus(authPrinciple.userId(), UserStatus.ACTIVE).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

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
	public ImageUploadResponseDto getPresignedUrl(AuthPrinciple authPrinciple, String fileName) {

		// 유저 찾기
		findUser(authPrinciple);

		// S3에 동일한 파일명이 이미 존재하는지 확인
		if (objectExists(fileName)) {
			throw new BizException(ImageErrorCode.CONFLICT_IMAGE);
		}

		// PutObjectRequest 정의
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(fileName)
			.contentType(getContentType(fileName))
			.build();

		// Presign 요청 생성 (5분 유효)
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(EXPIRE_MINUTES))
			.putObjectRequest(putObjectRequest)
			.build();

		URL presignedUrl = s3Presigner.presignPutObject(presignRequest).url();

		return ImageUploadResponseDto.from(presignedUrl);
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
