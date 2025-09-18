package project.flipnote.image.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.image.entity.Image;
import project.flipnote.image.entity.ImageMeta;
import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.entity.ReferenceType;
import project.flipnote.image.exception.ImageErrorCode;
import project.flipnote.image.model.ImageUploadResponseDto;
import project.flipnote.image.repository.ImageRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

	@Value("${cloud.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region}")
	private String region;

	@Value("${image.default.group}")
	private String defaultGroupImage;

	@Value("${image.default.user}")
	private String defaultUserImage;

	@Value("${image.default.cardSet}")
	private String defaultCardSetImage;

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
		String key = "image/" + fileName;
		log.info(hash);

		// DB에 동일한 파일명이 이미 존재하는지 확인
		Optional<Image> existImage = imageRepository.findByHash(hash);
		if(existImage.isPresent()) {
			ImageRef imageRef = ImageRef.builder()
				.image(existImage.get())
				.build();

			imageRefService.save(imageRef);

			URL url = generateUrl(existImage.get().getS3Key());

			return ImageUploadResponseDto.from(url, imageRef.getId());
		}

		// PutObjectRequest 정의
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucket)
			.key(key)
			.contentType(getContentType(fileName))
			.build();

		// Presigned 요청 생성 (5분 유효)
		PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(EXPIRE_MINUTES))
			.putObjectRequest(putObjectRequest)
			.build();

		URL presignedUrl = s3Presigner.presignPutObject(presignRequest).url();

		log.info(presignedUrl.toString());

		Image image = Image.builder()
			.hash(hash)
			.s3Key(key)
			.build();

		imageRepository.save(image);

		ImageRef imageRef = ImageRef.builder()
			.image(image)
			.build();

		imageRefService.save(imageRef);

		return ImageUploadResponseDto.from(presignedUrl, imageRef.getId());
	}

	public void changeUrlStatus(Long id, ReferenceType type, Long referenceId) {

		//이미지 참조 아이디 찾기
		ImageRef imageRef = imageRefService.findById(id).orElseThrow(
			() -> new BizException(ImageErrorCode.IMAGE_NOT_FOUND)
		);

		//이미지 사용중으로 변경
		imageRefService.imageActivate(imageRef.getId(), type, referenceId);

		//이미지 조회
		Image image = imageRepository.findById(imageRef.getImage().getId()).orElseThrow(
			() -> new BizException(ImageErrorCode.IMAGE_NOT_FOUND)
		);

		if (!StringUtils.hasText(image.getMimeType())) {
			//S3에서 메타데이터 가져오기
			HeadObjectResponse headResponse = s3Client.headObject(
				HeadObjectRequest.builder()
					.bucket(bucket)
					.key(image.getS3Key()) // 저장된 파일명 (Key)
					.build()
			);

			//메타 데이터 저장
			String mimeType = headResponse.contentType();      // ex) "image/jpeg"
			Long sizeBytes = headResponse.contentLength();     // 파일 크기 (byte 단위)

			image.updateMetadata(mimeType, sizeBytes);

			imageRepository.save(image);
		}
	}

	//키를 통한 이미지 url 생성
	public URL generateUrl(String key) {
		try {
			URL url = new URL("https://" + bucket + ".s3." + region + ".amazonaws.com/" + key);
			return  url;
		}
		catch (MalformedURLException e) {
			throw new BizException(ImageErrorCode.INVALID_URL);
		}
	}

	public String getURLByReferenceId(ReferenceType type, Long referenceId) {
		Image image = imageRepository.findImageByReferenceId(type, referenceId).orElseThrow(
			() -> new BizException(ImageErrorCode.IMAGE_NOT_FOUND)
		);

		URL url = generateUrl(image.getS3Key());

		return url.toString();
	}

	@Transactional
	public ImageMeta changeImage(ReferenceType type, Long referenceId, Long imageRefId) {

		Optional<ImageRef> imageRef = imageRefService.findByTypeAndReferenceId(type, referenceId);

		if(imageRefId==null) {
			if(imageRef.isPresent()) {
				imageRefService.delete(imageRef.get());
			}

			String url = getDefaultImage(type);

			return ImageMeta.from(null, url);

		}

		// 신규 imageRef가 이미 다른 대상에 묶여있는지 선검증
		ImageRef targetRef = imageRefService.findById(imageRefId).orElseThrow(
			() -> new BizException(ImageErrorCode.IMAGE_NOT_FOUND)
		);
		if (targetRef.getReferenceId() != null &&
			!(type.equals(targetRef.getReferenceType()) && referenceId.equals(targetRef.getReferenceId()))) {
			throw new BizException(ImageErrorCode.CONFLICT_IMAGE_REF);
		}

		if(imageRef.isPresent()) {
			if (imageRef.get().getId().equals(imageRefId)) {
				String url = getURLByReferenceId(type, referenceId);

				return ImageMeta.from(imageRef.get().getId(), url);
			}
			imageRefService.delete(imageRef.get());
		}

		imageRefService.imageActivate(imageRefId, type, referenceId);

		String url = getURLByReferenceId(type, referenceId);

		return ImageMeta.from(imageRefId, url);
	}

	private String getDefaultImage(ReferenceType type) {
		return switch (type) {
			case USER -> defaultUserImage;
			case GROUP -> defaultGroupImage;
			case CARD_SET -> defaultCardSetImage;
		};
	}

	public String assignImageUrl(ReferenceType type, Long imageRefId) {
		String url = getDefaultImage(type);
		Optional<ImageRef> imageRef = Optional.empty();

		if(imageRefId != null) {
			imageRef = imageRefService.findById(imageRefId);
		}

		if(imageRef.isPresent()) {
			Image image = imageRef.get().getImage();
			if(imageRef.get().getReferenceId()!=null) {
				throw new BizException(ImageErrorCode.CONFLICT_IMAGE_REF);
			}
			url = generateUrl(image.getS3Key()).toString();
		}

		return url;
	}



}
