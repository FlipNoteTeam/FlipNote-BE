package project.flipnote.bookmark.service.fetcher;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.cardset.service.CardSetService;

@RequiredArgsConstructor
@Component
public class BookmarkCardSetFetcher implements BookmarkTargetFetcher {

	private final CardSetService cardSetService;

	@Override
	public BookmarkTargetType getTargetType() {
		return BookmarkTargetType.CARD_SET;
	}

	@Override
	public boolean existsById(Long targetId) {
		return cardSetService.existsById(targetId);
	}
}
