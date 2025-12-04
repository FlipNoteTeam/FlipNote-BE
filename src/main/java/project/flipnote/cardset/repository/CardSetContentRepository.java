package project.flipnote.cardset.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.cardset.entity.CardSetContent;

public interface CardSetContentRepository extends JpaRepository<CardSetContent, Long> {
	void deleteByCardSetId(Long cardSetId);
}
