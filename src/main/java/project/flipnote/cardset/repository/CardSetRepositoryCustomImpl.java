package project.flipnote.cardset.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.cardset.entity.CardSet;
import project.flipnote.cardset.entity.QCardSet;
import project.flipnote.cardset.entity.QCardSetMetadata;
import project.flipnote.cardset.model.CardSetSortField;
import project.flipnote.group.entity.Category;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CardSetRepositoryCustomImpl implements CardSetRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	private final QCardSet cardSet = QCardSet.cardSet;
	private final QCardSetMetadata cardSetMetadata = QCardSetMetadata.cardSetMetadata;

	@Override
	public Page<CardSet> searchByNameContainingAndCategory(
		String name,
		Category category,
		Pageable pageable
	) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();

		boolean useMetadata = false;
		boolean hasIdSort = false;
		for (Sort.Order order : pageable.getSort()) {
			CardSetSortField sortField = null;
			try {
				sortField = CardSetSortField.valueOf(order.getProperty());
			} catch (IllegalArgumentException iae) {
				log.warn(
					"Unknown sort property: {}. Valid values are {}",
					order.getProperty(), Arrays.toString(CardSetSortField.values()), iae
				);
			}
			if (sortField == CardSetSortField.LIKE) {
				orders.add(toOrderSpecifier(cardSetMetadata.likeCount, order));
				useMetadata = true;
			} else {
				orders.add(toOrderSpecifier(cardSet.id, order));
				hasIdSort = true;
			}
		}

		if (!hasIdSort) {
			orders.add(cardSet.id.desc());
		}

		JPAQuery<CardSet> selectQuery = queryFactory
			.select(cardSet)
			.from(cardSet)
			.where(buildCardSetSearchFilterConditions(name, category));

		if (useMetadata) {
			selectQuery.join(cardSetMetadata).on(cardSet.id.eq(cardSetMetadata.id));
		}

		List<CardSet> content = selectQuery
			.orderBy(orders.toArray(new OrderSpecifier[0]))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		Long total = queryFactory
			.select(cardSet.count())
			.from(cardSet)
			.where(buildCardSetSearchFilterConditions(name, category))
			.fetchOne();

		return new PageImpl<>(content, pageable, total != null ? total : 0L);
	}

	private OrderSpecifier<?> toOrderSpecifier(
		NumberPath<?> path,
		Sort.Order order
	) {
		return order.isAscending() ? path.asc() : path.desc();
	}

	private BooleanExpression nameContains(String name) {
		return StringUtils.hasText(name) ? cardSet.name.contains(name) : null;
	}

	private BooleanExpression categoryEquals(Category category) {
		return category == null ? null : cardSet.category.eq(category);
	}

	private BooleanExpression[] buildCardSetSearchFilterConditions(String name, Category category) {
		return new BooleanExpression[]{
			nameContains(name),
			categoryEquals(category),
			cardSet.publicVisible.isTrue()
		};
	}
}
