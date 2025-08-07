package project.flipnote.cardset.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.cardset.entity.CardSet;
import project.flipnote.cardset.entity.CardSetManager;
import project.flipnote.cardset.model.CreateCardSetRequest;
import project.flipnote.cardset.model.CreateCardSetResponse;
import project.flipnote.cardset.repository.CardSetManagerRepository;
import project.flipnote.cardset.repository.CardSetRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.group.entity.Group;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.groupjoin.exception.GroupJoinErrorCode;
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

	private Boolean existGroupMember(Group group, UserProfile user) {
		return groupMemberRepository.findByGroup_idAndUser_id((group.getId()), user.getId());
	}

	@Transactional
	public CreateCardSetResponse createCardSet(Long groupId, AuthPrinciple authPrinciple, CreateCardSetRequest req) {

		//유저 정보 찾기
		UserProfile user = validateUser(authPrinciple.userId());

		//그룹 정보 찾기
		Group group = findGroup(groupId);
		
		//그룹 내 유저 있는지 확인
		if (!existGroupMember(group, user)) {
			throw new BizException(GroupJoinErrorCode.USER_NOT_IN_GROUP);
		}

		//해시태그가 없으면 null로 저장
		String hashtags = (req.hashtag() != null && !req.hashtag().isEmpty())
			? String.join(",", req.hashtag())
			: null;

		CardSet cardSet = CardSet.builder()
			.name(req.name())
			.publicVisible(req.publicVisible())
			.category(req.category())
			.hashtag(hashtags)
			.imageUrl(req.image())
			.build();

		cardSetRepository.save(cardSet);

		//카드셋 매니저도 저장
		CardSetManager cardSetManager = CardSetManager.builder()
			.user(user)
			.cardSet(cardSet)
			.build();

		cardSetManagerRepository.save(cardSetManager);


		return CreateCardSetResponse.from(cardSet.getId());
	}
}
