package project.flipnote.cardset.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import project.flipnote.cardset.entity.CardSet;
import project.flipnote.group.entity.Category;

@Repository
public interface CardSetRepository extends JpaRepository<CardSet, Long> {

	@Query("""
		SELECT c FROM CardSet c
		WHERE (:name IS NULL OR c.name LIKE CONCAT('%', :name, '%'))
		AND (:category IS NULL OR c.category = :category)
		AND c.publicVisible = TRUE
		""")
	Page<CardSet> findByNameContainingAndCategory(
		@Param("name") String name,
		@Param("category") Category category,
		Pageable pageable
	);

	Optional<CardSet> findByIdAndGroup_Id(Long id, Long groupId);

	Set<Long> findAllByGroup_IdAndPublicVisibleFalse(Long groupId);
}
