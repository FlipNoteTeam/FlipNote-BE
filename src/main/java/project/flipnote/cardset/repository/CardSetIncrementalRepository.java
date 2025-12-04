package project.flipnote.cardset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.cardset.entity.CardSetIncremental;

public interface CardSetIncrementalRepository extends JpaRepository<CardSetIncremental, Long> {
	void deleteByCardSetId(Long cardSetId);
}
