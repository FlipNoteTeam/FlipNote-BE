package project.flipnote.cardset.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.cardset.entity.CardSet;
import project.flipnote.cardset.entity.CardSetManager;
import project.flipnote.cardset.entity.CardSetMetadata;
import project.flipnote.cardset.exception.CardSetErrorCode;
import project.flipnote.cardset.model.CardSetDetailResponse;
import project.flipnote.cardset.model.CardSetInfo;
import project.flipnote.cardset.model.CardSetSearchRequest;
import project.flipnote.cardset.model.CardSetSummaryResponse;
import project.flipnote.cardset.model.CardSetUpdatePayload;
import project.flipnote.cardset.model.CardSetUpdateRequest;
import project.flipnote.cardset.model.CreateCardSetRequest;
import project.flipnote.cardset.model.CreateCardSetResponse;
import project.flipnote.cardset.repository.CardSetManagerRepository;
import project.flipnote.cardset.repository.CardSetMetadataRepository;
import project.flipnote.cardset.repository.CardSetRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.model.response.PagingResponse;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.image.entity.Image;
import project.flipnote.image.entity.ImageMeta;
import project.flipnote.image.entity.ImageRef;
import project.flipnote.image.entity.ReferenceType;
import project.flipnote.image.exception.ImageErrorCode;
import project.flipnote.image.service.ImageRefService;
import project.flipnote.image.service.ImageService;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserProfileRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardSetService {

	private final CardSetRepository cardSetRepository;
	private final UserProfileRepository userProfileRepository;
	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final CardSetManagerRepository cardSetManagerRepository;
	private final CardSetPolicyService cardSetPolicyService;
	private final CardSetMetadataRepository cardSetMetadataRepository;
	private final ImageService imageService;
	private final ImageRefService imageRefService;

	@Value("${image.default.cardSet}")
	private String defaultCardSetImage;

	private static final ReferenceType REFERENCE_TYPE = ReferenceType.CARD_SET;

	private UserProfile validateUser(Long userId) {
		return userProfileRepository.findByIdAndStatus(userId, UserStatus.ACTIVE).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	private Group findGroup(Long groupId) {
		return groupRepository.findById(groupId).orElseThrow(
			() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND)
		);
	}

	private boolean existGroupMember(Group group, UserProfile user) {
		return groupMemberRepository.existsByGroup_idAndUser_id((group.getId()), user.getId());
	}

	@Transactional
	public CreateCardSetResponse createCardSet(Long groupId, AuthPrinciple authPrinciple, CreateCardSetRequest req) {

		//유저 정보 찾기
		UserProfile user = validateUser(authPrinciple.userId());

		//그룹 정보 찾기
		Group group = findGroup(groupId);

		//그룹 내 유저 있는지 확인
		if (!existGroupMember(group, user)) {
			throw new BizException(CardSetErrorCode.GROUP_MEMBER_NOT_FOUND);
		}

		//해시태그가 없으면 null로 저장
		String hashtags = (req.hashtag() != null && !req.hashtag().isEmpty())
			? String.join(",", req.hashtag())
			: null;

		//이미지 url 찾기
		String url = imageService.assignImageUrl(REFERENCE_TYPE, req.imageRefId());

		CardSet cardSet = CardSet.builder()
			.name(req.name())
			.group(group)
			.publicVisible(req.publicVisible())
			.category(req.category())
			.hashtag(hashtags)
			.imageUrl(url)
			.build();

		cardSetRepository.save(cardSet);

		if(req.imageRefId()!=null) {
			// 이미지 활성화
			imageService.changeUrlStatus(req.imageRefId(), REFERENCE_TYPE, cardSet.getId());
		}

		CardSetMetadata metadata = CardSetMetadata.builder()
			.id(cardSet.getId())
			.build();
		cardSetMetadataRepository.save(metadata);

		//카드셋 매니저도 저장
		CardSetManager cardSetManager = CardSetManager.builder()
			.user(user)
			.cardSet(cardSet)
			.build();

		cardSetManagerRepository.save(cardSetManager);

		return CreateCardSetResponse.from(cardSet.getId());
	}

	/**
	 * 카드셋 목록을 페이지 단위로 조회
	 *
	 * @param req 조회 조건 및 페이징 정보를 포함한 요청 DTO
	 * @return 페이지 단위로 조회된 카드셋 목록
	 * @author 윤정환
	 */
	public PagingResponse<CardSetSummaryResponse> getCardSets(CardSetSearchRequest req) {
		// TODO: Projection 튜닝 필요
		Page<CardSetInfo> cardSetPage = cardSetRepository.searchByNameContainingAndCategory(
				req.getKeyword(), Category.from(req.getCategory()), req.getPageRequest()
		);

		Page<CardSetSummaryResponse> res = cardSetPage.map(CardSetSummaryResponse::from);

		return PagingResponse.from(res);
	}

	/**
	 * 카드셋 상세 조회
	 *
	 * @param userId    카드셋 상세 조회하는 회원 ID
	 * @param groupId   카드셋을 생성한 그룹 ID
	 * @param cardSetId 상세 조회하려는 카드셋 ID
	 * @return 카드셋 상세 조회 정보
	 * @author 윤정환
	 */
	public CardSetDetailResponse getCardSet(Long userId, Long groupId, Long cardSetId) {
		CardSet cardSet = cardSetPolicyService.findByIdAndGroupIdOrThrow(groupId, cardSetId);

		cardSetPolicyService.validateCardSetViewable(cardSet, userId);

		Optional<ImageRef> imageRef = imageRefService.findByTypeAndReferenceId(REFERENCE_TYPE, cardSetId);

		Long imageRefId = imageRefService.findByTypeAndReferenceId(REFERENCE_TYPE, cardSetId)
			.map(ImageRef::getId)
			.orElse(null);

		return CardSetDetailResponse.from(cardSet, imageRefId);
	}

	/**
	 * 카드셋 수정
	 *
	 * @param userId    카드셋 수정하는 회원 ID
	 * @param groupId   카드셋을 생성한 그룹 ID
	 * @param cardSetId 수정하려는 카드셋 ID
	 * @param req       카드셋의 수정 내용을 담은 요청 정보
	 * @return 수정된 카드셋 정보
	 * @author 윤정환
	 */
	@Transactional
	public CardSetDetailResponse updateCardSet(Long userId, Long groupId, Long cardSetId, CardSetUpdateRequest req) {
		CardSet cardSet = cardSetPolicyService.findByIdAndGroupIdOrThrow(groupId, cardSetId);

		cardSetPolicyService.validateCardSetEditable(userId, cardSetId);

		ImageMeta imageMeta = imageService.changeImage(REFERENCE_TYPE, cardSetId, req.imageRefId());

		CardSetUpdatePayload updatePayload = CardSetUpdatePayload.from(req);
		cardSet.update(updatePayload, imageMeta.url());

		cardSetRepository.saveAndFlush(cardSet);

		return CardSetDetailResponse.from(cardSet, imageMeta.imageRefId());
	}

	/**
	 * 카드셋 존재 여부 확인
	 *
	 * @param cardSetId 존재하는지 확인할 카드셋 ID
	 * @return 카드셋 존재 여부
	 * @author 윤정환
	 */
	public boolean existsById(Long cardSetId) {
		return cardSetRepository.existsById(cardSetId);
	}

	/**
	 * 카드셋 좋아요 수를 1 증가
	 *
	 * @param cardSetId 좋아요 수를 증가시킬 카드셋 ID
	 * @author 윤정환
	 */
	@Transactional
	public void incrementLikeCount(Long cardSetId) {
		cardSetMetadataRepository.incrementLikeCount(cardSetId);
	}

	/**
	 * 카드셋 좋아요 수를 1 감소
	 *
	 * @param cardSetId 좋아요 수를 감소시킬 카드셋 ID
	 * @author 윤정환
	 */
	@Transactional
	public void decrementLikeCount(Long cardSetId) {
		cardSetMetadataRepository.decrementLikeCount(cardSetId);
	}

	/**
	 * 카드셋 ID 목록에 해당하는 카드셋 목록 조회
	 *
	 * @param targetIds 조회할 카드셋 ID 목록
	 * @return 조회된 카드셋 목록
	 * @author 윤정환
	 */
	@Transactional
	public List<CardSetSummaryResponse> getCardSetsByIds(Set<Long> targetIds) {
		// TODO: MSA로 전환시 전용 DTO로 변경 필요
		return cardSetRepository.findAllByIdWithImageRefId(targetIds).stream()
			.map(CardSetSummaryResponse::from)
			.toList();
	}

	/**
	 * 사용자가 특정 카드셋에 접근할 수 있는지 여부를 확인
	 *
	 * @param cardSetId 확인할 카드셋의 ID
	 * @param userId    접근 권한을 확인할 사용자의 ID
	 * @return 접근 가능 여부
	 * @author 윤정환
	 */
	public boolean isCardSetViewable(Long cardSetId, Long userId) {
		return cardSetRepository.findById(cardSetId)
			.map(cardSet -> cardSetPolicyService.isCardSetViewable(cardSet, userId))
			.orElse(false);
	}

	/**
	 * 카드셋 ID 목록에 해당하는 카드셋 목록 조회
	 *
	 * @param targetIds 조회할 카드셋 ID 목록
	 * @param userId    카드셋 목록을 조회하는 회원 ID
	 * @return 조회된 카드셋 목록
	 * @author 윤정환
	 */
	@Transactional
	public List<CardSetSummaryResponse> findViewableCardSetsByIds(Set<Long> targetIds, Long userId) {
		// TODO: MSA로 전환시 전용 DTO로 변경 필요
		return cardSetRepository.findAllByIdWithImageRefId(targetIds).stream()
			.filter(cardSetInfo -> cardSetPolicyService.isCardSetViewable(cardSetInfo.cardSet(), userId))
			.map(CardSetSummaryResponse::from)
			.toList();
	}

	/**
	 * 해당 그룹의 비공개인 카드셋의 ID들을 조회
	 *
	 * @param groupId 조회할 그룹의 ID
	 * @return 그룹에 속한 비공개 카드셋 ID의 집합
	 * @author 윤정환
	 */
	public Set<Long> findPrivateCardSetIds(Long groupId) {
		return cardSetRepository.findPrivateIdsByGroupId(groupId);
	}

	/**
	 * 카드셋 즐겨찾기 수를 1 증가
	 *
	 * @param cardSetId 즐겨찾기 수를 증가시킬 카드셋 ID
	 * @author 윤정환
	 */
	@Transactional
	public void incrementBookmarkCount(Long cardSetId) {
		cardSetMetadataRepository.incrementBookmarkCount(cardSetId);
	}

	/**
	 * 카드셋 즐겨찾기 수를 1 감소
	 *
	 * @param cardSetId 즐겨찾기 수를 감소시킬 카드셋 ID
	 * @author 윤정환
	 */
	@Transactional
	public void decrementBookmarkCount(Long cardSetId) {
		cardSetMetadataRepository.decrementBookmarkCount(cardSetId);
	}

	/**
	 * 여러 카드셋 즐겨찾기 수를 1 감소
	 *
	 * @param cardSetIds 즐겨찾기 수를 감소시킬 카드셋 ID 목록
	 * @author 윤정환
	 */
	@Transactional
	public void decrementBookmarkCount(List<Long> cardSetIds) {
		cardSetMetadataRepository.decrementBookmarkCount(cardSetIds);
	}

	/**
	 * 특정 그룹의 카드셋 목록을 페이지 단위로 조회
	 *
	 * @param groupId 조회할 그룹의 ID
	 * @param req 조회 조건 및 페이징 정보를 포함한 요청 DTO
	 * @return 페이지 단위로 조회된 카드셋 목록
	 * @author 윤정환
	 */
	public PagingResponse<CardSetSummaryResponse> getCardSets(long groupId, CardSetSearchRequest req) {
		// TODO: Projection 튜닝 필요
		Page<CardSetInfo> cardSetPage = cardSetRepository.searchByGroupIdAndNameContainingAndCategory(
			groupId, req.getKeyword(), Category.from(req.getCategory()), req.getPageRequest()
		);

		Page<CardSetSummaryResponse> res = cardSetPage.map(CardSetSummaryResponse::from);

		return PagingResponse.from(res);
	}
}
