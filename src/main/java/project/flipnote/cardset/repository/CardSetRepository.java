package project.flipnote.cardset.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.cardset.entity.CardSet;

@Repository
public interface CardSetRepository extends JpaRepository<CardSet, Long>, CardSetRepositoryCustom {
	Optional<CardSet> findByIdAndGroup_Id(Long id, Long groupId);
}
