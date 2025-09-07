package project.flipnote.image.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.image.entity.ImageRef;

@Repository
public interface ImageRefRepository extends JpaRepository<ImageRef, Long> {
}
