package project.flipnote.cardset.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import project.flipnote.cardset.entity.CardSetManager;

@Repository
public interface CardSetManagerRepository extends JpaRepository<CardSetManager, Long> {

	boolean existsByUser_IdAndCardSet_Id(Long userId, Long cardSetId);

	int deleteByCardSet_Id(Long cardSetId);

	@Query("SELECT cm.user.id FROM CardSetManager cm WHERE cm.cardSet.id = :cardSetId")
	Set<Long> findUserIdsByCardSetId(Long cardSetId);

	int deleteByCardSet_IdAndUser_IdIn(Long cardSetId, Set<Long> userIds);
}
