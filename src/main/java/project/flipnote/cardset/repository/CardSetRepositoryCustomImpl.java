package project.flipnote.cardset.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.entity.CardSet;
import project.flipnote.cardset.entity.QCardSet;
import project.flipnote.cardset.entity.QCardSetMetadata;
import project.flipnote.cardset.model.CardSetSortField;
import project.flipnote.group.entity.Category;

@RequiredArgsConstructor
@Repository
public class CardSetRepositoryCustomImpl implements CardSetRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<CardSet> findByNameContainingAndCategory(
		String name,
		Category category,
		Pageable pageable
	) {
		QCardSet c = QCardSet.cardSet;
		QCardSetMetadata m = QCardSetMetadata.cardSetMetadata;

		List<OrderSpecifier<?>> orders = new ArrayList<>();

		boolean useMetadata = false;
		boolean hasIdSort = false;
		for (Sort.Order order : pageable.getSort()) {
			CardSetSortField sortField = CardSetSortField.valueOf(order.getProperty());
			if (sortField == CardSetSortField.LIKE) {
				orders.add(toOrderSpecifier(m.likeCount, order));
				useMetadata = true;
			} else {
				orders.add(toOrderSpecifier(c.id, order));
				hasIdSort = true;
			}
		}

		if (!hasIdSort) {
			orders.add(c.id.desc());
		}

		JPAQuery<CardSet> selectQuery = queryFactory
			.select(c)
			.from(c)
			.where(
				StringUtils.hasText(name) ? c.name.contains(name) : null,
				category == null ? null : c.category.eq(category),
				c.publicVisible.isTrue()
			);

		if (useMetadata) {
			selectQuery.join(m).on(c.id.eq(m.id));
		}

		List<CardSet> content = selectQuery
			.orderBy(orders.toArray(new OrderSpecifier[0]))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = queryFactory
			.select(c.count())
			.from(c)
			.where(
				StringUtils.hasText(name) ? c.name.contains(name) : null,
				category == null ? null : c.category.eq(category),
				c.publicVisible.isTrue()
			)
			.fetchOne();

		return new PageImpl<>(content, pageable, total);
	}

	private OrderSpecifier<?> toOrderSpecifier(
		NumberPath<?> path,
		Sort.Order order
	) {
		return order.isAscending() ? path.asc() : path.desc();
	}
}
