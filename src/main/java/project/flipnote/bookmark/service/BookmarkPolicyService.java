package project.flipnote.bookmark.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.exception.BookmarkErrorCode;
import project.flipnote.bookmark.repository.BookmarkRepository;
import project.flipnote.common.exception.BizException;

@RequiredArgsConstructor
@Service
public class BookmarkPolicyService {

	private final BookmarkRepository bookmarkRepository;
	private final BookmarkTargetFetchService bookmarkTargetFetchService;

	public void validateTargetExists(BookmarkTargetType targetType, Long targetId) {
		if (!bookmarkTargetFetchService.existsByTypeAndId(targetType, targetId)) {
			throw new BizException(BookmarkErrorCode.BOOKMARK_TARGET_NOT_FOUND);
		}
	}

	public void validateBookmarkNotExists(BookmarkTargetType targetType, Long targetId, Long userId) {
		if (bookmarkRepository.existsByTargetTypeAndTargetIdAndUserId(targetType, targetId, userId)) {
			throw new BizException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
		}
	}
}
