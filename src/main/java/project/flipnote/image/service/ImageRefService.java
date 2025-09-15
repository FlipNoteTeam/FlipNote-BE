package project.flipnote.image.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.entity.ReferenceType;
import project.flipnote.image.exception.ImageErrorCode;
import project.flipnote.image.repository.ImageRefRepository;

@Service
@RequiredArgsConstructor
public class ImageRefService {
	private final ImageRefRepository imageRefRepository;

	public void save(ImageRef imageRef) {
		imageRefRepository.save(imageRef);
	}

	public Optional<ImageRef> findById(Long id) {
		return imageRefRepository.findById(id);
	}

	public Optional<ImageRef> findByTypeAndReferenceId(ReferenceType type, Long referenceId) {
		return imageRefRepository.findByReferenceTypeAndReferenceId(type, referenceId);
	}

	public void imageActivate(Long imageRefId, ReferenceType type, Long referenceId) {

		ImageRef imageRef = findById(imageRefId).orElseThrow(
			() -> new BizException(ImageErrorCode.IMAGE_NOT_FOUND)
		);

		imageRef.activateFor(type, referenceId);
		imageRefRepository.save(imageRef);
	}

	public void delete(ImageRef imageRef) {
		imageRefRepository.delete(imageRef);
	}

	public void deleteByReferenceAndId(ReferenceType type, Long id) {
		Optional<ImageRef> imageRef = imageRefRepository.findByReferenceTypeAndReferenceId(type, id);

		if(imageRef.isPresent()) {
			delete(imageRef.get());
		}
	}
}
