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

import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.fixture.UserFixture;
import project.flipnote.group.entity.Group;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.groupjoin.entity.GroupJoin;
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

	User user;
	UserAuth userAuth;
	Group group;

	@BeforeEach
	void before() {
		user = UserFixture.createActiveUser();
		userAuth = new UserAuth(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());
		group = Group.builder().name("123").imageUrl("12312").publicVisible(true).applicationRequired(true).build();

		// 사용자 검증 로직
		given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
		given(groupRepository.findById(1L)).willReturn(Optional.of(group));
	}

	@Test
	void 가입신청_요청_성공() {
		//given
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
		assertEquals(res.groupJoinId(), 1L);
	}

	@Test
	void findGroupJoinList() {
	}

	@Test
	void respondToJoinRequest() {
	}

	@Test
	void groupJoinDelete() {
	}

	@Test
	void findGroupJoinListMe() {
	}
}
