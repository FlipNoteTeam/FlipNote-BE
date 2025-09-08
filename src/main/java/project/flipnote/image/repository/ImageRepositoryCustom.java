package project.flipnote.image.repository;

import java.util.Optional;

import project.flipnote.image.entity.Image;
import project.flipnote.image.entity.ReferenceType;

public interface ImageRepositoryCustom {
	public Optional<Image> findImageByReferenceId(ReferenceType type, Long referenceId);
}
