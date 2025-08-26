package project.flipnote.group.repository;

import java.util.List;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.QGroup;
import project.flipnote.group.entity.QGroupMember;
import project.flipnote.group.model.GroupInfo;

@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	QGroup group = QGroup.group;
	QGroupMember groupMember = QGroupMember.groupMember;

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
				group.imageUrl
			))
			.from(group)
			.where(where)
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
				group.imageUrl
			))
			.from(group)
			.join(groupMember.group, group)
			.where(groupMember.group.id.eq(group.id).and(groupMember.user.id.eq(userId)).and(where))
			.orderBy(group.id.desc())
			.limit(pageSize+1)
			.fetch();
	}

}
