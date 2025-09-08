package project.flipnote.image.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.flipnote.image.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long>, ImageRepositoryCustom {
	Optional<Image> findByHash(String fileName);
}
