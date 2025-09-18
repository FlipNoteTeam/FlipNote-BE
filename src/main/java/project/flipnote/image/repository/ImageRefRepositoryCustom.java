package project.flipnote.image.repository;

import java.time.LocalDateTime;
import java.util.List;

import project.flipnote.image.entity.ImageRef;

public interface ImageRefRepositoryCustom {
	public List<ImageRef> findExpiredPending(Long lastId, LocalDateTime cutoffTime, int batchSize);
}
