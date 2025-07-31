package project.flipnote.groupjoin.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.lang.reflect.Field;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.fixture.UserFixture;
import project.flipnote.group.entity.Group;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.groupjoin.entity.GroupJoin;
import project.flipnote.groupjoin.entity.GroupJoinStatus;
import project.flipnote.groupjoin.exception.GroupJoinErrorCode;
import project.flipnote.groupjoin.model.GroupJoinRequest;
import project.flipnote.groupjoin.model.GroupJoinResponse;
import project.flipnote.groupjoin.repository.GroupJoinRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class GroupJoinServiceTest {

	@InjectMocks
	GroupJoinService groupJoinService;

	@Mock
	UserRepository userRepository;

	@Mock
	GroupRepository groupRepository;

	@Mock
	GroupJoinRepository groupJoinRepository;

	@Mock
	GroupMemberRepository groupMemberRepository;

	User user;
	UserAuth userAuth;
	Group group;

	@BeforeEach
	void before() {
		user = UserFixture.createActiveUser();
		userAuth = new UserAuth(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());

		// 사용자 검증 로직
		given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
	}

	@Test
	void 가입신청_요청_성공() {
		//given
		group = Group.builder().name("123").imageUrl("12312").publicVisible(true).applicationRequired(true).build();
		given(groupRepository.findById(1L)).willReturn(Optional.of(group));
		GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
		when(groupJoinRepository.save(any())).thenAnswer(invocation -> {
			GroupJoin join = invocation.getArgument(0);

			// reflection으로 id 필드 설정
			Field idField = GroupJoin.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(join, 1L);

			return join;
		});

		//when
		GroupJoinResponse res = groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest);

		//then
		assertEquals(1L, res.groupJoinId());
		assertEquals(GroupJoinStatus.PENDING, res.status());
	}

	@Test
	void 가입신청_요청_성공_가입_신청_필수가아닐경우() {
		//given
		group = Group.builder().name("123").imageUrl("12312").publicVisible(true).applicationRequired(false).build();
		given(groupRepository.findById(1L)).willReturn(Optional.of(group));
		GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
		when(groupJoinRepository.save(any())).thenAnswer(invocation -> {
			GroupJoin join = invocation.getArgument(0);

			// reflection으로 id 필드 설정
			Field idField = GroupJoin.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.set(join, 1L);

			return join;
		});

		//when
		GroupJoinResponse res = groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest);

		//then
		assertEquals(GroupJoinStatus.ACCEPT, res.status());
	}

	@Test
	void 가입신청_요청_실패_그룹_존재_없을_경우() {
		// given
		GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");

		//when
		when(groupRepository.findById(2L))
			.thenReturn(Optional.empty());

		//then
		BizException exception = assertThrows(
			BizException.class,
			() -> groupJoinService.joinRequest(userAuth, 2L, groupJoinRequest)
		);

		assertEquals(GroupErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());
	}

	@Test
	void 가입신청_요청_실패_그룹_비공개일_경우() {
		// given
		GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
		group = Group.builder().name("123").imageUrl("12312").publicVisible(false).applicationRequired(true).build();
		given(groupRepository.findById(1L)).willReturn(Optional.of(group));

		// when

		// then
		BizException exception = assertThrows(
			BizException.class,
			() -> groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest)
		);

		assertEquals(GroupJoinErrorCode.GROUP_IS_NOT_PUBLIC, exception.getErrorCode());
	}

	@Test
	void 가입신청_요청_실패_최대인원인_경우() {
		// given
		GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");
		group = Group.builder().maxMember(100).name("123").imageUrl("12312").publicVisible(true).applicationRequired(true).build();
		given(groupRepository.findById(1L)).willReturn(Optional.of(group));
		given(groupMemberRepository.countByGroup_Id(any())).willReturn(200L);
		// when

		// then
		BizException exception = assertThrows(
			BizException.class,
			() -> groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest)
		);

		assertEquals(GroupJoinErrorCode.GROUP_IS_ALREADY_MAX_MEMBER, exception.getErrorCode());
	}

	@Test
	void 가입신청_요청_실패_이미_신청한_경우() {
		// given
		group = Group.builder()
			.name("123")
			.maxMember(100)
			.imageUrl("12312")
			.publicVisible(true)
			.applicationRequired(true)
			.build();
		given(groupRepository.findById(1L)).willReturn(Optional.of(group));

		GroupJoinRequest groupJoinRequest = new GroupJoinRequest("안녕하세요.");

		GroupJoin alreadyJoined = GroupJoin.builder()
			.group(group)
			.user(user)
			.joinIntro("이미 있음")
			.status(GroupJoinStatus.PENDING)
			.build();
		// 이미 신청한 이력이 있다고 가정
		given(groupJoinRepository.existsByGroup_idAndUser_id(group.getId(), user.getId())).willReturn(true);

		// when
		BizException exception = assertThrows(
			BizException.class,
			() -> groupJoinService.joinRequest(userAuth, 1L, groupJoinRequest)
		);

		// then
		assertEquals(GroupJoinErrorCode.ALREADY_JOINED_GROUP, exception.getErrorCode());
	}

}
