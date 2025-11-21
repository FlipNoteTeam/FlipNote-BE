package project.flipnote.group.repository;

import java.util.List;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.entity.QGroup;
import project.flipnote.group.entity.QGroupMember;
import project.flipnote.group.model.GroupInfo;
import project.flipnote.image.entity.QImageRef;
import project.flipnote.image.entity.ReferenceType;

@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QGroup group = QGroup.group;
	QGroupMember groupMember = QGroupMember.groupMember;
	QImageRef imageRef = QImageRef.imageRef;

	@Override
	public List<GroupInfo> findAllByCursor(Long lastId, Category category, int pageSize) {
		BooleanBuilder where = new BooleanBuilder()
			.and(group.deletedAt.isNull());

		if (lastId != null) {
			where.and(group.id.lt(lastId));
		}

		if (category != null) {
			where.and(group.category.eq(category));
		}

		return queryFactory.select(Projections.constructor(
				GroupInfo.class,
				group.id,
				group.name,
				group.description,
				group.category,
				group.imageUrl,
				imageRef.id
			))
			.from(group)
			.where(where)
			.leftJoin(imageRef)
			.on(imageRef.referenceType.eq(ReferenceType.GROUP)
				.and(imageRef.referenceId.eq(group.id)))
			.orderBy(group.id.desc())
			.limit(pageSize+1)
			.fetch();
	}

	@Override
	public List<GroupInfo> findAllByCursorAndUserId(Long lastId, Category category, int pageSize, Long userId) {
		BooleanBuilder where = new BooleanBuilder()
			.and(group.deletedAt.isNull());

		if (lastId != null) {
			where.and(group.id.lt(lastId));
		}

		if (category != null) {
			where.and(group.category.eq(category));
		}

		return queryFactory.select(Projections.constructor(
				GroupInfo.class,
				group.id,
				group.name,
				group.description,
				group.category,
				group.imageUrl,
				imageRef.id
			))
			.from(groupMember)
			.join(groupMember.group, group)
			.on(groupMember.user.id.eq(userId))
			.where(where)
			.leftJoin(imageRef)
			.on(imageRef.referenceType.eq(ReferenceType.GROUP)
				.and(imageRef.referenceId.eq(group.id)))
			.orderBy(group.id.desc())
			.limit(pageSize+1)
			.fetch();
	}

	/**
	 * 그룹 테이블에 생성한 유저 추가?
	 * @param lastId
	 * @param category
	 * @param pageSize
	 * @param userId
	 * @return
	 */
	@Override
	public List<GroupInfo> findAllByCursorAndCreatedUserId(Long lastId, Category category, int pageSize, Long userId) {
		return queryFactory
			.select(Projections.constructor(
				GroupInfo.class,
				group.id,
				group.name,
				group.description,
				group.category,
				group.imageUrl,
				imageRef.id
			))
			.from(group)
			.join(groupMember).on(groupMember.group.eq(group))
			.where(
				group.deletedAt.isNull(),
				groupMember.user.id.eq(userId),
				groupMember.role.eq(GroupMemberRole.OWNER),
				lastId != null ? group.id.lt(lastId) : null,
				category != null ? group.category.eq(category) : null
			)
			.leftJoin(imageRef)
			.on(imageRef.referenceType.eq(ReferenceType.GROUP)
				.and(imageRef.referenceId.eq(group.id)))
			.orderBy(group.id.desc())
			.limit(pageSize + 1)
			.fetch();
	}

}
