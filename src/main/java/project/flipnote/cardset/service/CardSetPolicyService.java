package project.flipnote.cardset.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.cardset.entity.CardSet;
import project.flipnote.cardset.exception.CardSetErrorCode;
import project.flipnote.cardset.repository.CardSetManagerRepository;
import project.flipnote.cardset.repository.CardSetRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.group.service.GroupService;

@RequiredArgsConstructor
@Service
public class CardSetPolicyService {

	private final CardSetRepository cardSetRepository;
	private final CardSetManagerRepository cardSetManagerRepository;
	private final GroupService groupService;

	/**
	 * 그룹 ID와 카드셋 ID로 카드셋을 조회
	 *
	 * @param groupId   조회할 카드셋이 속한 그룹의 ID
	 * @param cardSetId 조회할 카드셋의 ID
	 * @return          조회된 카드셋 엔티티
	 * @author 윤정환
	 */
	public CardSet findByIdAndGroupIdOrThrow(Long groupId, Long cardSetId) {
		return cardSetRepository.findByIdAndGroup_Id(cardSetId, groupId)
			.orElseThrow(() -> new BizException(CardSetErrorCode.CARD_SET_NOT_FOUND));
	}

	/**
	 * 특정 회원이 해당 카드셋을 수정할 수 있는 권한이 있는지 검증
	 *
	 * @param userId    수정 권한을 검증할 회원의 ID
	 * @param cardSetId 수정 권한이 요구되는 카드셋의 ID
	 * @author 윤정환
	 */
	public void validateCardSetEditable(Long userId, Long cardSetId) {
		if (!cardSetManagerRepository.existsByUser_IdAndCardSet_Id(userId, cardSetId)) {
			throw new BizException(CardSetErrorCode.CARD_SET_NO_EDIT_PERMISSION);
		}
	}

	/**
	 * 특정 회원이 해당 카드셋을 조회할 수 있는 권한이 있는지 검증
	 *
	 * @param cardSet 조회 대상 카드셋 엔티티
	 * @param userId 조회 권한을 검증할 회원의 ID
	 * @author 윤정환
	 */
	public void validateCardSetViewable(CardSet cardSet, Long userId) {
		if (!isCardSetViewable(cardSet, userId)) {
			throw new BizException(CardSetErrorCode.CARD_SET_PRIVATE);
		}
	}

	/**
	 * 특정 회원이 해당 카드셋을 조회할 수 있는 권한이 있는지 확인
	 *
	 * @param cardSet 조회 대상 카드셋 엔티티
	 * @param userId 조회 권한을 검증할 회원의 ID
	 * @return 카드셋 조회 가능 여부
	 * @author 윤정환
	 */
	public boolean isCardSetViewable(CardSet cardSet, Long userId) {
		return cardSet.getPublicVisible() || groupService.existsMember(cardSet.getGroup().getId(), userId);
	}
}
