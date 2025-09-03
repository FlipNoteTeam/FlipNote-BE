package project.flipnote.cardset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.flipnote.cardset.entity.CardSetMetadata;

public interface CardSetMetadataRepository extends JpaRepository<CardSetMetadata, Long> {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("UPDATE CardSetMetadata m SET m.likeCount = m.likeCount + 1 WHERE m.id = :cardSetId")
	int incrementLikeCount(@Param("cardSetId") Long cardSetId);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		UPDATE CardSetMetadata m
		SET m.likeCount = CASE WHEN m.likeCount > 0 THEN m.likeCount - 1 ELSE 0 END
		WHERE m.id = :cardSetId
		""")
	int decrementLikeCount(@Param("cardSetId") Long cardSetId);
}
