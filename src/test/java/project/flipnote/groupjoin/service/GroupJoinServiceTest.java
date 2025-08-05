package project.flipnote.groupjoin.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;
import java.nio.file.attribute.UserPrincipal;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import project.flipnote.common.exception.BizException;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupPermissionRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.groupjoin.entity.GroupJoin;
import project.flipnote.groupjoin.entity.GroupJoinStatus;
import project.flipnote.groupjoin.exception.GroupJoinErrorCode;
import project.flipnote.groupjoin.model.GroupJoinRequest;
import project.flipnote.groupjoin.model.GroupJoinRespondRequest;
import project.flipnote.groupjoin.model.GroupJoinRespondResponse;
import project.flipnote.groupjoin.model.GroupJoinResponse;
import project.flipnote.groupjoin.repository.GroupJoinRepository;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.repository.UserProfileRepository;

@ExtendWith(MockitoExtension.class)
class GroupJoinServiceTest {

	@InjectMocks
	GroupJoinService groupJoinService;

	@Mock
	UserProfileRepository userProfileRepository;

	@Mock
	GroupRepository groupRepository;

	@Mock
	GroupJoinRepository groupJoinRepository;

	@Mock
	GroupMemberRepository groupMemberRepository;

	@Mock
	GroupPermissionRepository groupPermissionRepository;

	UserProfile userProfile;
	UserPrincipal userPrincipal;
	Group group;

	@BeforeEach
	void before() {
		// user = UserFixture.createActiveUser();
		// userAuth = new UserAuth(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());
		//
		// // 사용자 검증 로직
		// given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
	}
	//
	// @Test
	// void 가입신청_요청_성공() {
	// 	//given
	// 	group = Group.builder().name("123").imageUrl("12312").publicVisible(true).applicationRequired(true).build();
	// 	given(groupRepository.findById(1L)).willReturn(Optional.of(group));
	// 	GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
	// 	when(groupJoinRepository.save(any())).thenAnswer(invocation -> {
	// 		GroupJoin join = invocation.getArgument(0);
	//
	// 		// reflection으로 id 필드 설정
	// 		Field idField = GroupJoin.class.getDeclaredField("id");
	// 		idField.setAccessible(true);
	// 		idField.set(join, 1L);
	//
	// 		return join;
	// 	});
	//
	// 	//when
	// 	GroupJoinResponse res = groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest);
	//
	// 	//then
	// 	assertEquals(1L, res.groupJoinId());
	// 	assertEquals(GroupJoinStatus.PENDING, res.status());
	// }
	//
	// @Test
	// void 가입신청_요청_성공_가입_신청_필수가아닐경우() {
	// 	//given
	// 	group = Group.builder().name("123").imageUrl("12312").publicVisible(true).applicationRequired(false).build();
	// 	given(groupRepository.findById(1L)).willReturn(Optional.of(group));
	// 	GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
	// 	when(groupJoinRepository.save(any())).thenAnswer(invocation -> {
	// 		GroupJoin join = invocation.getArgument(0);
	//
	// 		// reflection으로 id 필드 설정
	// 		Field idField = GroupJoin.class.getDeclaredField("id");
	// 		idField.setAccessible(true);
	// 		idField.set(join, 1L);
	//
	// 		return join;
	// 	});
	//
	// 	//when
	// 	GroupJoinResponse res = groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest);
	//
	// 	//then
	// 	assertEquals(GroupJoinStatus.ACCEPT, res.status());
	// }
	//
	// @Test
	// void 가입신청_요청_실패_그룹_존재_없을_경우() {
	// 	// given
	// 	GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
	//
	// 	//when
	// 	when(groupRepository.findById(2L))
	// 		.thenReturn(Optional.empty());
	//
	// 	//then
	// 	BizException exception = assertThrows(
	// 		BizException.class,
	// 		() -> groupJoinService.joinRequest(userAuth, 2L, groupJoinRequest)
	// 	);
	//
	// 	assertEquals(GroupErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());
	// }
	//
	// @Test
	// void 가입신청_요청_실패_그룹_비공개일_경우() {
	// 	// given
	// 	GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
	// 	group = Group.builder().name("123").imageUrl("12312").publicVisible(false).applicationRequired(true).build();
	// 	given(groupRepository.findById(1L)).willReturn(Optional.of(group));
	//
	// 	// when
	//
	// 	// then
	// 	BizException exception = assertThrows(
	// 		BizException.class,
	// 		() -> groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest)
	// 	);
	//
	// 	assertEquals(GroupJoinErrorCode.GROUP_IS_NOT_PUBLIC, exception.getErrorCode());
	// }
	//
	// @Test
	// void 가입신청_요청_실패_최대인원인_경우() {
	// 	// given
	// 	GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
	// 	group = Group.builder().maxMember(100).name("123").imageUrl("12312").publicVisible(true).applicationRequired(true).build();
	// 	given(groupRepository.findById(1L)).willReturn(Optional.of(group));
	// 	given(groupMemberRepository.countByGroup_Id(any())).willReturn(200L);
	// 	// when
	//
	// 	// then
	// 	BizException exception = assertThrows(
	// 		BizException.class,
	// 		() -> groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest)
	// 	);
	//
	// 	assertEquals(GroupJoinErrorCode.GROUP_IS_ALREADY_MAX_MEMBER, exception.getErrorCode());
	// }
	//
	// @Test
	// void 가입신청_요청_실패_이미_신청한_경우() {
	// 	// given
	// 	group = Group.builder()
	// 		.name("123")
	// 		.maxMember(100)
	// 		.imageUrl("12312")
	// 		.publicVisible(true)
	// 		.applicationRequired(true)
	// 		.build();
	// 	given(groupRepository.findById(1L)).willReturn(Optional.of(group));
	//
	// 	GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
	//
	// 	GroupJoin alreadyJoined = GroupJoin.builder()
	// 		.group(group)
	// 		.user(user)
	// 		.joinIntro("이미 있음")
	// 		.status(GroupJoinStatus.PENDING)
	// 		.build();
	// 	// 이미 신청한 이력이 있다고 가정
	// 	given(groupJoinRepository.existsByGroup_idAndUser_id(group.getId(), user.getId())).willReturn(true);
	//
	// 	// when
	// 	BizException exception = assertThrows(
	// 		BizException.class,
	// 		() -> groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest)
	// 	);
	//
	// 	// then
	// 	assertEquals(GroupJoinErrorCode.ALREADY_JOINED_GROUP, exception.getErrorCode());
	// }
	//
	// @Test
	// void 가입신청_삭제_성공_본인_신청내역_취소() throws Exception {
	// 	// given
	// 	group = Group.builder().build();
	// 	Field idField = Group.class.getDeclaredField("id");
	// 	idField.setAccessible(true);
	// 	idField.set(group, 1L);
	//
	// 	GroupJoin groupJoin = GroupJoin.builder()
	// 		.group(group)
	// 		.user(user)
	// 		.status(GroupJoinStatus.PENDING)
	// 		.build();
	//
	// 	// 리플렉션으로 ID 강제 주입
	// 	idField = GroupJoin.class.getDeclaredField("id");
	// 	idField.setAccessible(true);
	// 	idField.set(groupJoin, 1L);
	//
	// 	given(groupJoinRepository.findById(1L)).willReturn(Optional.of(groupJoin));
	//
	// 	// when
	// 	assertDoesNotThrow(() -> groupJoinService.groupJoinDelete(userAuth, 1L, 1L));
	//
	// 	// then
	// 	assertEquals(GroupJoinStatus.CANCEL, groupJoin.getStatus());
	// 	verify(groupJoinRepository).save(any());
	// }
	//
	// @Test
	// void 가입신청_삭제_실패_본인_아님() throws Exception {
	// 	// given
	// 	// 그룹 생성
	// 	group = Group.builder().build();
	// 	Field groupIdField = Group.class.getDeclaredField("id");
	// 	groupIdField.setAccessible(true);
	// 	groupIdField.set(group, 1L);
	//
	// 	// 가입 신청자의 유저 (user1)
	// 	User user1 = User.builder()
	// 		.email("USER_EMAIL")
	// 		.password("ENCODED_PASSWORD")
	// 		.nickname("테스트닉네임")
	// 		.name("테스트이름")
	// 		.phone("+821012345678")
	// 		.smsAgree(true)
	// 		.profileImageUrl("test_image_url")
	// 		.build();
	//
	// 	ReflectionTestUtils.setField(user1, "id", 2L);
	//
	// 	// 로그인한 사용자 (userAuth.user ≠ user1)
	// 	// user는 테스트 클래스의 필드에 이미 있음
	//
	// 	GroupJoin groupJoin = GroupJoin.builder()
	// 		.group(group)
	// 		.user(user1) // 신청자는 user1
	// 		.status(GroupJoinStatus.PENDING)
	// 		.build();
	//
	// 	// ID 주입
	// 	Field joinIdField = GroupJoin.class.getDeclaredField("id");
	// 	joinIdField.setAccessible(true);
	// 	joinIdField.set(groupJoin, 1L);
	//
	// 	// when: userAuth (user)가 user1의 신청을 삭제 시도
	// 	given(groupJoinRepository.findById(1L)).willReturn(Optional.of(groupJoin));
	//
	// 	// then
	// 	BizException exception = assertThrows(
	// 		BizException.class,
	// 		() -> groupJoinService.groupJoinDelete(userAuth, 1L, 1L)
	// 	);
	//
	// 	assertEquals(GroupJoinErrorCode.USER_NOT_PERMISSION, exception.getErrorCode());
	// }

}
