package project.flipnote.bookmark.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.Bookmark;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.exception.BookmarkErrorCode;
import project.flipnote.bookmark.repository.BookmarkRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.response.IdResponse;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkService {

	private final BookmarkPolicyService bookmarkPolicyService;
	private final BookmarkRepository bookmarkRepository;

	/**
	 * 즐겨찾기 추가
	 *
	 * @param userId 즐겨찾기 추가 요청한 사용자 ID
	 * @param targetType 즐겨찾기 대상 타입
	 * @param targetId 즐겨찾기 대상 ID
	 * @return 생성된 즐겨찾기 대상의 ID를 담은 응답
	 * @author 윤정환
	 */
	@Transactional
	public IdResponse addBookmark(Long userId, BookmarkTargetType targetType, Long targetId) {
		bookmarkPolicyService.validateBookmarkNotExists(targetType, targetId, userId);
		bookmarkPolicyService.validateTargetExists(targetType, targetId);

		Bookmark bookmark = Bookmark.builder()
			.targetType(targetType)
			.targetId(targetId)
			.userId(userId)
			.build();

		try {
			bookmarkRepository.save(bookmark);
		} catch (DataIntegrityViolationException e) {
			throw new BizException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
		}

		return IdResponse.from(bookmark.getId());
	}

	/**
	 * 즐겨찾기 제거
	 *
	 * @param userId 즐겨찾기 제거 요청한 회원 ID
	 * @param targetType 즐겨찾기 대상 타입
	 * @param targetId 즐겨찾기 대상 ID
	 * @return 삭제된 즐겨찾기 대상의 ID를 담은 응답
	 * @author 윤정환
	 */
	@Transactional
	public IdResponse deleteBookmark(Long userId, BookmarkTargetType targetType, Long targetId) {
		Bookmark bookmark = bookmarkRepository.findByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId)
			.orElseThrow(() -> new BizException(BookmarkErrorCode.BOOKMARK_NOT_EXISTS));

		bookmarkRepository.delete(bookmark);

		return IdResponse.from(bookmark.getId());
	}
}
