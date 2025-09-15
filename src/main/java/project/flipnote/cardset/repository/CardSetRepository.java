package project.flipnote.cardset.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.cardset.entity.CardSet;

@Repository
public interface CardSetRepository extends JpaRepository<CardSet, Long>, CardSetRepositoryCustom {
	Optional<CardSet> findByIdAndGroup_Id(Long id, Long groupId);

	@Query("""
		SELECT c.id FROM CardSet c
		WHERE c.group.id = :groupId
		AND c.publicVisible = false
		""")
	Set<Long> findPrivateIdsByGroupId(@Param("groupId") Long groupId);
}
