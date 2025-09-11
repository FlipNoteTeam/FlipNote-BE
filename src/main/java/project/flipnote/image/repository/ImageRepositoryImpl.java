package project.flipnote.image.repository;

import java.util.List;
import java.util.Optional;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.flipnote.image.entity.Image;
import project.flipnote.image.entity.ImageStatus;
import project.flipnote.image.entity.QImage;
import project.flipnote.image.entity.QImageRef;
import project.flipnote.image.entity.ReferenceType;
import project.flipnote.image.model.ImageIdKey;

@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QImage image = QImage.image;
	QImageRef imageRef = QImageRef.imageRef;

	@Override
	public Optional<Image> findImageByReferenceId(ReferenceType type, Long referenceId) {
		Image result = queryFactory
			.select(image)
			.from(imageRef)
			.join(imageRef.image, image)
			.where(
				imageRef.referenceType.eq(type),
				imageRef.referenceId.eq(referenceId),
				imageRef.status.eq(ImageStatus.USING)
			)
			.orderBy(imageRef.id.desc())
			.limit(1)
			.fetchOne();

		return Optional.ofNullable(result);
	}

	@Override
	public List<ImageIdKey> findOrphanCandidates(Long lastId, int batchSize) {
		return queryFactory
			.select(Projections.constructor(
				ImageIdKey.class,
				image.id,
				image.s3Key
				))
			.from(image)
			.where(
				lastId != null ? image.id.gt(lastId) : null,
				JPAExpressions.selectOne()
					.from(imageRef)
					.where(
						imageRef.image.eq(image)
					).notExists()
			)
			.orderBy(image.id.asc())
			.limit(batchSize)
			.fetch();
	}
}
