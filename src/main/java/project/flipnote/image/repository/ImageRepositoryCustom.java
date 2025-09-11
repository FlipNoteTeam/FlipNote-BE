package project.flipnote.image.repository;

import java.util.List;
import java.util.Optional;

import project.flipnote.image.entity.Image;
import project.flipnote.image.entity.ReferenceType;
import project.flipnote.image.model.ImageIdKey;

public interface ImageRepositoryCustom {
	public Optional<Image> findImageByReferenceId(ReferenceType type, Long referenceId);

	List<ImageIdKey> findOrphanCandidates(Long lastId, int batchSize);
}
