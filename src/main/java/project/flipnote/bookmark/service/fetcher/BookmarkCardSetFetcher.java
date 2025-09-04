package project.flipnote.bookmark.service.fetcher;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import project.flipnote.bookmark.entity.BookmarkTargetType;
import project.flipnote.bookmark.model.CardSetBookmarkResponse;
import project.flipnote.cardset.service.CardSetService;

@RequiredArgsConstructor
@Component
public class BookmarkCardSetFetcher implements BookmarkTargetFetcher<CardSetBookmarkResponse> {

	private final CardSetService cardSetService;

	@Override
	public BookmarkTargetType getTargetType() {
		return BookmarkTargetType.CARD_SET;
	}

	@Override
	public boolean isTargetViewable(Long targetId, Long userId) {
		return cardSetService.isCardSetViewable(targetId, userId);
	}

	@Override
	public Map<Long, CardSetBookmarkResponse> fetchByIds(Set<Long> ids) {
		return cardSetService.getCardSetsByIds(ids).stream()
			.map(CardSetBookmarkResponse::from)
			.collect(Collectors.toMap(CardSetBookmarkResponse::getId, Function.identity()));
	}
}
