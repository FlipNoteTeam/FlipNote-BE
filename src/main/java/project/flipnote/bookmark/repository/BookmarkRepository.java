package project.flipnote.bookmark.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import project.flipnote.bookmark.entity.Bookmark;
import project.flipnote.bookmark.entity.BookmarkTargetType;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	boolean existsByTargetTypeAndUserIdAndTargetId(BookmarkTargetType targetType, Long userId, Long targetId);

	Optional<Bookmark> findByTargetTypeAndUserIdAndTargetId(BookmarkTargetType targetType, Long userId, Long targetId);

	Page<Bookmark> findAllByTargetTypeAndUserId(BookmarkTargetType targetType, Long userId, Pageable pageable);
}
