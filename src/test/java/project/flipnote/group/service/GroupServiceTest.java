package project.flipnote.group.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.util.ReflectionTestUtils;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.fixture.UserFixture;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupPermission;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupPermissionRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.group.repository.GroupRolePermissionRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

	private static final Logger log = LoggerFactory.getLogger(GroupServiceTest.class);
	@InjectMocks
	GroupService groupService;

	@Mock
	GroupRepository groupRepository;

	@Mock
	GroupPermissionRepository groupPermissionRepository;

	@Mock
	GroupRolePermissionRepository groupRolePermissionRepository;

	@Mock
	UserRepository userRepository;

	@Mock
	EmailVerificationRedisRepository emailVerificationRedisRepository;

	@Mock
	GroupMemberRepository groupMemberRepository;

	User user;
	UserAuth userAuth;

	@BeforeEach
	void before() {
		user = UserFixture.createActiveUser();
		userAuth = new UserAuth(user.getId(), user.getEmail(), user.getRole(), user.getTokenVersion());

		// 사용자 검증 로직
		given(userRepository.findByIdAndStatus(user.getId(), UserStatus.ACTIVE)).willReturn(Optional.of(user));
	}

	@Test
	void 그룹_생성_성공() {
		// given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");
		Group group = Group.builder().name(req.name()).build();
		ReflectionTestUtils.setField(group, "id", 1L);

		given(groupRepository.save(any(Group.class))).willReturn(group);

		// 그룹 퍼미션 미리 세팅
		List<GroupPermission> permissions = List.of(
				GroupPermission.builder().name("INVITE").build(),
				GroupPermission.builder().name("KICK").build(),
				GroupPermission.builder().name("JOIN_REQUEST_MANAGE").build()
		);
		given(groupPermissionRepository.findAll()).willReturn(permissions);

		// when
		GroupCreateResponse response = groupService.create(userAuth, req);

		// then
		assertThat(response.groupId()).isEqualTo(1L);
	}

	@Test
	void 그룹_생성_실패_음수() {
		// given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, -100, "www.~~~");
		Group group = Group.builder().name(req.name()).build();
		ReflectionTestUtils.setField(group, "id", 1L);


		// when & then
		assertThrows(BizException.class, () -> groupService.create(userAuth, req));
	}

	@Test
	void 그룹_생성_실패_초과() {
		// given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, 200, "www.~~~");
		Group group = Group.builder().name(req.name()).build();
		ReflectionTestUtils.setField(group, "id", 1L);

		// when & then
		assertThrows(BizException.class, () -> groupService.create(userAuth, req));
	}
}
