package project.flipnote.image.service;

import java.net.URL;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.image.exception.ImageErrorCode;
import project.flipnote.image.model.ImageUploadResponseDto;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

	@Value("${cloud.s3.bucket}")
	private String bucket;

	private final AmazonS3 amazonS3;
	private final UserRepository userRepository;

	private User findUser(AuthPrinciple authPrinciple) {
		return userRepository.findByIdAndStatus(authPrinciple.userId(), UserStatus.ACTIVE).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	//presigned url  생성
	public ImageUploadResponseDto getPresignedUrl(AuthPrinciple authPrinciple, String fileName) {
		
		//유저 찾기
		findUser(authPrinciple);

		// S3에 동일한 파일명이 이미 존재하는지 확인
		if (amazonS3.doesObjectExist(bucket, fileName)) {
			throw new BizException(ImageErrorCode.CONFLICT_IMAGE); //
		}

		//presignedUrl 생성
		GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePresignedUrlRequest(fileName);
		//url 생성
		URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

		return ImageUploadResponseDto.from(url);
	}

	private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String fileName) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
			.withMethod(HttpMethod.PUT)
			.withExpiration(getPresignedUrlExpiration());

		generatePresignedUrlRequest.addRequestParameter(
			Headers.S3_CANNED_ACL,
			CannedAccessControlList.PublicRead.toString()
		);

		return generatePresignedUrlRequest;
	}

	private Date getPresignedUrlExpiration() {
		Date expiration = new Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 5;
		expiration.setTime(expTimeMillis);

		return expiration;
	}
}
