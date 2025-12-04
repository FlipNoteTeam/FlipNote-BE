package project.flipnote.bookmark.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.repository.BookmarkRepository;

@Service
@RequiredArgsConstructor
public class BookmarkReader {
	private final BookmarkRepository bookmarkRepository;

	/**
	 * 즐겨찾기를 했는지 여부를 조회합니다.
	 *
	 * @param userId      즐겨찾기를 했는지 확인할 회원 ID
	 * @param targetType  즐겨찾기 대상의 타입
	 * @param targetId    즐겨찾기 대상의 ID
	 * @return 타겟을 즐겨찾기 했으면 true, 아니면 false
	 * @author 윤정환
	 */
	public boolean isBookmarked(Long userId, BookmarkTargetType targetType, Long targetId) {
		return bookmarkRepository.existsByTargetTypeAndUserIdAndTargetId(targetType, userId, targetId);
	}
}
