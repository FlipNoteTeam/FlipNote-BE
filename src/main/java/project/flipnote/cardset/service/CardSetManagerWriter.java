package project.flipnote.cardset.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.entity.CardSet;
import project.flipnote.cardset.entity.CardSetManager;
import project.flipnote.cardset.repository.CardSetManagerRepository;
import project.flipnote.user.repository.UserProfileRepository;

@RequiredArgsConstructor
@Service
public class CardSetManagerWriter {

	private final CardSetManagerRepository cardSetManagerRepository;
	private final UserProfileRepository userProfileRepository;

	/**
	 * 카드셋에 매니저들을 할당
	 * 생성자는 항상 매니저로 포함됨
	 *
	 * @param cardSet    매니저를 할당할 카드셋
	 * @param managerIds 요청된 매니저 ID 목록
	 * @author 윤정환
	 */
	public void assignManagers(CardSet cardSet, Set<Long> managerIds) {
		Set<Long> finalManagerIds = includeAuthor(cardSet.getAuthor(), managerIds);

		List<CardSetManager> managers = finalManagerIds.stream()
			.map(id -> CardSetManager.builder()
				.cardSet(cardSet)
				.user(userProfileRepository.getReferenceById(id))
				.build())
			.toList();

		cardSetManagerRepository.saveAll(managers);
	}

	/**
	 * 카드셋의 매니저를 수정
	 * 차집합을 이용해 삭제/추가할 매니저만 처리
	 *
	 * @param cardSet       매니저를 수정할 카드셋
	 * @param newManagerIds 새로운 매니저 ID 목록
	 * @author 윤정환
	 */
	public void updateManagers(CardSet cardSet, Set<Long> newManagerIds) {
		Set<Long> currentManagerIds = cardSetManagerRepository.findUserIdsByCardSetId(cardSet.getId());

		Set<Long> toDelete = difference(currentManagerIds, newManagerIds);
		Set<Long> toAdd = difference(newManagerIds, currentManagerIds);

		if (!toDelete.isEmpty()) {
			cardSetManagerRepository.deleteByCardSet_IdAndUser_IdIn(cardSet.getId(), toDelete);
		}

		if (!toAdd.isEmpty()) {
			List<CardSetManager> managers = toAdd.stream()
				.map(id -> CardSetManager.builder()
					.cardSet(cardSet)
					.user(userProfileRepository.getReferenceById(id))
					.build())
				.toList();
			cardSetManagerRepository.saveAll(managers);
		}
	}

	private Set<Long> includeAuthor(Long authorId, Set<Long> managerIds) {
		Set<Long> result = new HashSet<>(managerIds);
		result.add(authorId);
		return result;
	}

	private Set<Long> difference(Set<Long> a, Set<Long> b) {
		Set<Long> result = new HashSet<>(a);
		result.removeAll(b);
		return result;
	}
}
