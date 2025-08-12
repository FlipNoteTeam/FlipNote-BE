package project.flipnote.group.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

import project.flipnote.auth.entity.AccountRole;
import project.flipnote.auth.repository.EmailVerificationRedisRepository;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.fixture.UserFixture;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupPermission;
import project.flipnote.group.entity.GroupPermissionStatus;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.model.GroupDetailResponse;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupPermissionRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.group.repository.GroupRolePermissionRepository;
import project.flipnote.groupjoin.exception.GroupJoinErrorCode;
import project.flipnote.user.entity.UserProfile;
import project.flipnote.user.entity.UserStatus;
import project.flipnote.user.repository.UserProfileRepository;

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
	UserProfileRepository userProfileRepository;

	@Mock
	EmailVerificationRedisRepository emailVerificationRedisRepository;

	@Mock
	GroupMemberRepository groupMemberRepository;

	UserProfile userProfile;
	AuthPrinciple authPrinciple;

	@BeforeEach
	void before() {
		userProfile = UserFixture.createActiveUser();
		authPrinciple = new AuthPrinciple(1L, userProfile.getId(), userProfile.getEmail(), AccountRole.USER, 1L);
	}

	@Test
	void 그룹_생성_성공() {
		// given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");
		Group group = Group.builder().name(req.name()).build();
		ReflectionTestUtils.setField(group, "id", 1L);

		given(groupRepository.save(any(Group.class))).willReturn(group);
		// 사용자 검증 로직
		given(userProfileRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE))
			.willReturn(Optional.of(userProfile));

		// 그룹 퍼미션 미리 세팅
		List<GroupPermission> permissions = List.of(
				GroupPermission.builder().name(GroupPermissionStatus.INVITE).build(),
				GroupPermission.builder().name(GroupPermissionStatus.KICK).build(),
				GroupPermission.builder().name(GroupPermissionStatus.JOIN_REQUEST_MANAGE).build()
		);
		given(groupPermissionRepository.findAll()).willReturn(permissions);

		// when
		GroupCreateResponse response = groupService.create(authPrinciple, req);

		// then
		assertThat(response.groupId()).isEqualTo(1L);
	}

	@Test
	void 그룹_생성_실패_음수() {
		// given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, -100, "www.~~~");
		Group group = Group.builder().name(req.name()).build();
		ReflectionTestUtils.setField(group, "id", 1L);
		// 사용자 검증 로직
		given(userProfileRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE))
			.willReturn(Optional.of(userProfile));

		// when & then
		assertThrows(BizException.class, () -> groupService.create(authPrinciple, req));
	}

	@Test
	void 그룹_생성_실패_초과() {
		// given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, 200, "www.~~~");
		Group group = Group.builder().name(req.name()).build();
		ReflectionTestUtils.setField(group, "id", 1L);
		// 사용자 검증 로직
		given(userProfileRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE))
			.willReturn(Optional.of(userProfile));

		// when & then
		assertThrows(BizException.class, () -> groupService.create(authPrinciple, req));
	}
	
	@Test
	public void 그룹_상세_조회_성공() throws Exception {
	    //given
		Group group = Group.builder()
			.name("그룹1")
			.category(Category.IT)
			.description("설명1")
			.publicVisible(true)
			.applicationRequired(true)
			.maxMember(100)
			.imageUrl("www.~~~")
			.build();

		given(groupRepository.findByIdAndDeletedAtIsNull(any())).willReturn(Optional.ofNullable(group));
		given(groupMemberRepository.existsByGroup_idAndUser_id(any(), any())).willReturn(true);
		// 사용자 검증 로직
		given(userProfileRepository.findByIdAndStatus(userProfile.getId(), UserStatus.ACTIVE))
			.willReturn(Optional.of(userProfile));

	    //when
		GroupDetailResponse res = groupService.findGroupDetail(authPrinciple, 1L);

		//then
		assertEquals("그룹1", res.name());
	}

	@Test
	public void 그룹_상세_조회_실패_그룹내_유저가_없는경우() throws Exception {
	    //given
		Group group = Group.builder()
			.name("그룹1")
			.category(Category.IT)
			.description("설명1")
			.publicVisible(true)
			.applicationRequired(true)
			.maxMember(100)
			.imageUrl("www.~~~")
			.build();

		given(groupRepository.findByIdAndDeletedAtIsNull(any())).willReturn(Optional.ofNullable(group));
		given(userProfileRepository.findByIdAndStatus(1L, UserStatus.ACTIVE)).willReturn(Optional.ofNullable(userProfile));
		given(groupMemberRepository.existsByGroup_idAndUser_id(any(), any())).willReturn(false);

	    //when
		BizException exception =
			assertThrows(BizException.class, () -> groupService.findGroupDetail(authPrinciple, 1L));

	    //then
		assertEquals(GroupJoinErrorCode.USER_NOT_IN_GROUP, exception.getErrorCode());
	}

	@Test
	public void 그룹_상세_조회_실패_삭제된_경우() throws Exception {
		//given
		Group group = Group.builder()
			.name("그룹1")
			.category(Category.IT)
			.description("설명1")
			.publicVisible(true)
			.applicationRequired(true)
			.maxMember(100)
			.imageUrl("www.~~~")
			.build();

		groupRepository.save(group);
		groupRepository.delete(group);

		//when & then
		BizException exception =
			assertThrows(BizException.class, () -> groupService.findGroupDetail(authPrinciple, 1L));

		assertEquals(GroupErrorCode.GROUP_NOT_FOUND, exception.getErrorCode());
	}
}
