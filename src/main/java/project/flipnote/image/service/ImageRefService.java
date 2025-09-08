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

	public void imageActivate(ImageRef imageRef, ReferenceType type, Long referenceId) {
		imageRef.activateFor(type, referenceId);
		imageRefRepository.save(imageRef);
	}
}
