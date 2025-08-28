package project.flipnote.like.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.common.entity.LikeType;
import project.flipnote.like.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByTypeAndTargetIdAndUserId(LikeType likeType, Long targetId, Long userId);

	Optional<Like> findByTypeAndTargetIdAndUserId(LikeType likeType, Long targetId, Long userId);

	Page<Like> findByTypeAndUserId(LikeType likeType, Long userId, Pageable pageable);
}
