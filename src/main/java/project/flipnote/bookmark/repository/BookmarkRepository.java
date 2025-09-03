package project.flipnote.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.bookmark.entity.Bookmark;
import project.flipnote.bookmark.entity.BookmarkTargetType;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	boolean existsByTargetTypeAndTargetIdAndUserId(BookmarkTargetType targetType, Long targetId, Long userId);

	Optional<Bookmark> findByTargetTypeAndTargetIdAndUserId(BookmarkTargetType targetType, Long targetId, Long userId);
}
