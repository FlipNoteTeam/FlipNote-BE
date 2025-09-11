package project.flipnote.image.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.model.ImageIdKey;
import project.flipnote.image.repository.ImageRefRepository;
import project.flipnote.image.repository.ImageRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageCleanService {

	@Value("${image-clean.batch-size}")
	private int batchSize;

	@Value("${cloud.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.region}")
	private String region;

	@Value("${image-clean.orphan-grace-minutes}")
	private int ORPHAN_GRACE_MINUTES;

	private final ImageRefRepository imageRefRepository;
	private final ImageRepository imageRepository;
	private final S3Client s3Client;

	/**
	 * 참조 테이블의 pending이 10분이상 지나면 제거
	 */
	@Transactional
	public void cleanImageRef() {
		LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(ORPHAN_GRACE_MINUTES);

		Long lastId = null;
		int deletedCount = 0;

		while (true) {
			List<ImageRef> refs = imageRefRepository.findExpiredPending(lastId, cutoffTime, batchSize);
			if (refs.isEmpty()) break;

			refs.forEach(imageRefRepository::delete);
			deletedCount += refs.size();

			lastId = refs.get(refs.size() - 1).getId();
		}

		log.info("cleanImageRef: deleted {} expired refs (cutoff={})", deletedCount, cutoffTime);
	}


	@Transactional
	public void cleanImage() {

		Long lastId = null;
		int deletedCount = 0;

		while (true) {
			List<ImageIdKey> images = imageRepository.findOrphanCandidates(lastId, batchSize);
			if (images.isEmpty())
				break;

			for (var row : images) {
				Long imageId = row.id();
				String s3Key = row.s3Key();

				// 레이스 재확인: 혹시 그 사이 참조가 생겼으면 스킵
				if (imageRefRepository.existsByImage_Id(imageId)) {
					lastId = imageId;
					continue;
				}

				// 1) S3 삭제 (트랜잭션 밖)
				try {
					s3Client.deleteObject(DeleteObjectRequest.builder()
						.bucket(bucket)
						.key(s3Key)
						.build());
				} catch (Exception e) {
					log.warn("S3 delete failed, keep DB for retry. imageId={}, key={}, err={}",
						imageId, s3Key, e.toString());
					lastId = imageId;
					continue; // 다음 항목으로 (DB는 남겨서 재시도)
				}

				// 2) DB 하드 삭제 (짧은 트랜잭션)
				try {
					hardDeleteImage(imageId);
					deletedCount++;
				} catch (Exception e) {
					log.warn("DB delete failed after S3 deletion. imageId={}, err={}", imageId, e.toString());
				}

				lastId = imageId;
			}
		}

		log.info("cleanImage: removed {} orphan images", deletedCount);
	}

	@Transactional
	protected void hardDeleteImage(Long imageId) {
		// 마지막 방어: 참조 재확인 (동시에 USING이 붙은 극단적 레이스)
		if (imageRefRepository.existsByImage_Id(imageId)) return;
		imageRepository.deleteById(imageId); // Image는 고아면 바로 하드 삭제
	}
}
