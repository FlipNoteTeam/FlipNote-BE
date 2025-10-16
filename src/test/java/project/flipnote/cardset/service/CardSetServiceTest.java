package project.flipnote.cardset.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import project.flipnote.auth.entity.AccountRole;
import project.flipnote.cardset.entity.CardSet;
import project.flipnote.cardset.exception.CardSetErrorCode;
import project.flipnote.cardset.model.CreateCardSetRequest;
import project.flipnote.cardset.model.CreateCardSetResponse;
import project.flipnote.cardset.repository.CardSetManagerRepository;
import project.flipnote.cardset.repository.CardSetMetadataRepository;
import project.flipnote.cardset.repository.CardSetRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.fixture.UserFixture;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class CardSetServiceTest {

	// @InjectMocks
	// CardSetService cardSetService;
	//
	// @Mock
	// UserProfileRepository userProfileRepository;
	//
	// @Mock
	// GroupRepository groupRepository;
	//
	// @Mock
	// CardSetRepository cardSetRepository;
	//
	// @Mock
	// GroupMemberRepository groupMemberRepository;
	//
	// @Mock
	// CardSetManagerRepository cardSetManagerRepository;
	//
	// @Mock
	// CardSetMetadataRepository cardSetMetadataRepository;
	//
	// UserProfile user;
	// AuthPrinciple authPrinciple;
	// Group group;

	// @BeforeEach
	// void before() {
	// 	user = UserFixture.createActiveUser();
	// 	authPrinciple = new AuthPrinciple(1L, user.getId(), user.getEmail(), AccountRole.USER, 1L);
	// 	group = Group.builder()
	// 		.name("aa")
	// 		.imageUrl("wwww.~~~")
	// 		.publicVisible(true)
	// 		.applicationRequired(true)
	// 		.description("dsfad")
	// 		.maxMember(100)
	// 		.category(Category.IT)
	// 		.build();
	//
	// 	given(userProfileRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
	// 	given(groupRepository.findById(any())).willReturn(Optional.of(group));
	// }
	//
	// @Test
	// public void 카드_생성_성공() throws Exception {
	// 	//given
	// 	CreateCardSetRequest req = new CreateCardSetRequest("1233", true, Category.IT, new ArrayList<>(
	// 		List.of("123", "456")), "www.aab.com");
	//
	// 	when(cardSetRepository.save(any())).thenAnswer(invocation -> {
	// 		CardSet cardSet = invocation.getArgument(0);
	//
	// 		// reflection으로 id 필드 설정
	// 		Field idField = CardSet.class.getDeclaredField("id");
	// 		idField.setAccessible(true);
	// 		idField.set(cardSet, 1L);
	//
	// 		return cardSet;
	// 	});
	// 	given(groupMemberRepository.existsByGroup_idAndUser_id((group.getId()), user.getId())).willReturn(true);
	//
	// 	//when
	// 	CreateCardSetResponse res = cardSetService.createCardSet(1L, authPrinciple, req);
	// 	//then
	// 	assertEquals(1L, res.cardSetId());
	// }
	//
	// @Test
	// public void 카드_생성_실패_내가_가입한_그룹이_아닌경우() throws Exception {
	// 	//given
	// 	CreateCardSetRequest req = new CreateCardSetRequest("1233", true, Category.IT, new ArrayList<>(
	// 		List.of("123", "456")), "www.aab.com");
	//
	// 	given(groupMemberRepository.existsByGroup_idAndUser_id((group.getId()), user.getId())).willReturn(false);
	//
	// 	//when
	// 	BizException exception = assertThrows(
	// 		BizException.class,
	// 		() -> cardSetService.createCardSet(1L, authPrinciple, req)
	// 	);
	//
	// 	//then
	// 	assertEquals(CardSetErrorCode.GROUP_MEMBER_NOT_FOUND, exception.getErrorCode());
	// }
}
