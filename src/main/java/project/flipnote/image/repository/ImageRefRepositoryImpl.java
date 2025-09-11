package project.flipnote.image.repository;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.entity.ImageStatus;
import project.flipnote.image.entity.QImageRef;

@RequiredArgsConstructor
public class ImageRefRepositoryImpl implements ImageRefRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QImageRef imageRef = QImageRef.imageRef;

	public List<ImageRef> findExpiredPending(Long lastId, LocalDateTime cutoffTime, int batchSize) {
		return queryFactory
			.selectFrom(imageRef)
			.where(
				imageRef.status.ne(ImageStatus.USING),
				imageRef.createdAt.loe(cutoffTime),
				lastId != null ? imageRef.id.gt(lastId) : null
			)
			.orderBy(imageRef.id.asc())
			.limit(batchSize)
			.fetch();
	}
}
