package project.flipnote.cardset.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import project.flipnote.cardset.model.CardSetInfo;
import project.flipnote.group.entity.Category;

public interface CardSetRepositoryCustom {

	Page<CardSetInfo> searchByNameContainingAndCategory(
		String name,
		Category category,
		Pageable pageable
	);

	List<CardSetInfo> findAllByIdWithImageRefId(Set<Long> cardSets);

	Page<CardSetInfo> searchByGroupIdAndNameContainingAndCategory(
		Long groupId,
		String name,
		Category category,
		Pageable pageable
	);
}
