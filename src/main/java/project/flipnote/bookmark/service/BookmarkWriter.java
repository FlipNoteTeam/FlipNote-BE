package project.flipnote.bookmark.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.repository.BookmarkRepository;

@Service
@RequiredArgsConstructor
public class BookmarkWriter {
	private final BookmarkRepository bookmarkRepository;

	/**
	 * 즐겨찾기를 삭제합니다.
	 *
	 * @param targetType  즐겨찾기 삭제할 대상의 타입
	 * @param targetId    즐겨찾기 삭제할 대상의 ID
	 * @author 윤정환
	 */
	public void delete(BookmarkTargetType targetType, Long targetId) {
		return bookmarkRepository.deleteByTar(targetType, targetId);
	}
}
