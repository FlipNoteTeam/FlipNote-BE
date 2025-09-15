package project.flipnote.cardset.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import project.flipnote.cardset.entity.CardSet;
import project.flipnote.group.entity.Category;

public interface CardSetRepositoryCustom {

	Page<CardSet> searchByNameContainingAndCategory(
		String name,
		Category category,
		Pageable pageable
	);
}
