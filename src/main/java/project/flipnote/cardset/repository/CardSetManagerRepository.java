package project.flipnote.cardset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.cardset.entity.CardSetManager;

@Repository
public interface CardSetManagerRepository extends JpaRepository<CardSetManager, Long> {
}
