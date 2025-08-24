package project.flipnote.group.repository;

import java.util.List;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.QGroup;
import project.flipnote.group.model.GroupInfo;

@RequiredArgsConstructor
public class GroupRepositoryImpl implements GroupRepositoryCustom {

	private static final int SIZE = 10;

	private final JPAQueryFactory queryFactory;

	QGroup group = QGroup.group;


	@Override
	public List<GroupInfo> findAllByCursor(Long lastId, Category category) {
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
				group.description
			))
			.from(group)
			.where(where)
			.orderBy(group.id.desc())
			.limit(SIZE+1)
			.fetch();
	}

}
