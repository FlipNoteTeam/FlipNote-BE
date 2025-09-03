package project.flipnote.like.service.fetcher;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.service.CardSetService;
import project.flipnote.like.entity.LikeTargetType;
import project.flipnote.like.model.CardSetLikeResponse;
import project.flipnote.like.model.LikeTargetResponse;

@RequiredArgsConstructor
@Component
public class CardSetFetcher implements LikeTargetFetcher<CardSetLikeResponse> {

	private final CardSetService cardSetService;

	@Override
	public LikeTargetType getTargetType() {
		return LikeTargetType.CARD_SET;
	}

	@Override
	public Map<Long, CardSetLikeResponse> fetchByIds(List<Long> ids) {
		return cardSetService.getCardSetsByIds(ids).stream()
			.map(CardSetLikeResponse::from)
			.collect(Collectors.toMap(LikeTargetResponse::getId, Function.identity()));
	}
}
