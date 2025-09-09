package project.flipnote.image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.entity.ReferenceType;

@Repository
public interface ImageRefRepository extends JpaRepository<ImageRef, Long> {
	Optional<ImageRef> findByReferenceTypeAndReferenceId(ReferenceType type, Long referenceId);
}
