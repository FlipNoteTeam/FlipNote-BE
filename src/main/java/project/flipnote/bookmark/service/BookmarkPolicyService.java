package project.flipnote.bookmark.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.exception.BookmarkErrorCode;
import project.flipnote.bookmark.model.BookmarkTargetResponse;
import project.flipnote.bookmark.repository.BookmarkRepository;
import project.flipnote.common.exception.BizException;

@RequiredArgsConstructor
@Service
public class BookmarkPolicyService {

	private final BookmarkRepository bookmarkRepository;
	private final BookmarkTargetFetchService<BookmarkTargetResponse> bookmarkTargetFetchService;

	public void validateTargetExists(BookmarkTargetType targetType, Long targetId) {
		if (!bookmarkTargetFetchService.existsByTypeAndId(targetType, targetId)) {
			throw new BizException(BookmarkErrorCode.BOOKMARK_TARGET_NOT_FOUND);
		}
	}

	public void validateBookmarkNotExists(BookmarkTargetType targetType, Long userId, Long targetId) {
		if (bookmarkRepository.existsByTargetTypeAndUserIdAndTargetId(targetType, userId, targetId)) {
			throw new BizException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
		}
	}
}
