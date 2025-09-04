package project.flipnote.bookmark.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.Bookmark;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.exception.BookmarkErrorCode;
import project.flipnote.bookmark.model.BookmarkResponse;
import project.flipnote.bookmark.model.BookmarkSearchRequest;
import project.flipnote.bookmark.model.BookmarkTargetResponse;
import project.flipnote.bookmark.repository.BookmarkRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.response.IdResponse;
import project.flipnote.common.model.response.PagingResponse;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BookmarkService {

	private final BookmarkPolicyService bookmarkPolicyService;
	private final BookmarkRepository bookmarkRepository;
	private final BookmarkTargetFetchService<BookmarkTargetResponse> bookmarkTargetFetchService;

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
		bookmarkPolicyService.validateBookmarkNotExists(targetType, userId, targetId);
		bookmarkPolicyService.validateTargetViewable(targetType, targetId, userId);

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
		Bookmark bookmark = bookmarkRepository.findByTargetTypeAndUserIdAndTargetId(targetType, userId, targetId)
			.orElseThrow(() -> new BizException(BookmarkErrorCode.BOOKMARK_NOT_EXISTS));

		bookmarkRepository.delete(bookmark);

		return IdResponse.from(bookmark.getId());
	}

	/**
	 * 즐겨찾기 목록 조회
	 *
	 * @param userId 즐겨찾기 목록 조회하는 회원 ID
	 * @param targetType 즐겨찾기 목록 대상 타입
	 * @param req 페이징 및 검색 조건이 포함된 요청 정보
	 * @return 페이징된 즐겨찾기 목록
	 * @author 윤정환
	 */
	public PagingResponse<BookmarkResponse<BookmarkTargetResponse>> getBookmarks(
		Long userId,
		BookmarkTargetType targetType,
		BookmarkSearchRequest req
	) {
		Page<Bookmark> bookmarkPage
			= bookmarkRepository.findAllByTargetTypeAndUserId(targetType, userId, req.getPageRequest());
		Map<Long, LocalDateTime> likedAtMap = bookmarkPage.stream()
			.collect(Collectors.toMap(Bookmark::getTargetId, Bookmark::getCreatedAt));
		Set<Long> targetIds = likedAtMap.keySet();

		Map<Long, BookmarkTargetResponse> targetMap
			= bookmarkTargetFetchService.fetchByTypeAndIds(targetType, targetIds, userId);
		Page<BookmarkResponse<BookmarkTargetResponse>> content
			= bookmarkPage.map(bookmark ->
			new BookmarkResponse<>(
				targetMap.get(bookmark.getTargetId()),
				likedAtMap.get(bookmark.getTargetId())
			)
		);

		return PagingResponse.from(content);
	}
}
