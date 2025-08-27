package project.flipnote.like.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.like.entity.Like;
import project.flipnote.common.entity.LikeType;

public interface LikeRepository extends JpaRepository<Like, Long> {
	boolean existsByTypeAndTargetIdAndUserId(LikeType likeType, Long targetId, Long userId);
}
