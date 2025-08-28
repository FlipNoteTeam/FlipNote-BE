package project.flipnote.like.service.fetcher;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.service.CardSetService;
import project.flipnote.common.entity.LikeType;
import project.flipnote.like.model.CardSetLikeResponse;

@RequiredArgsConstructor
@Component
public class CardSetFetcher implements LikeTargetFetcher<CardSetLikeResponse> {

	private final CardSetService cardSetService;

	@Override
	public LikeType getLikeType() {
		return LikeType.CARD_SET;
	}

	@Override
	public List<CardSetLikeResponse> fetchByIds(List<Long> ids) {
		return cardSetService.getCardSetsByIds(ids).stream()
			.map(CardSetLikeResponse::from)
			.toList();
	}
}
