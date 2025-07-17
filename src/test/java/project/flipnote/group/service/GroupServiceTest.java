package project.flipnote.group.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.GroupPermission;
import project.flipnote.group.entity.GroupRolePermission;
import project.flipnote.group.model.GroupCreateRequest;
import project.flipnote.group.model.GroupCreateResponse;
import project.flipnote.group.repository.GroupPermissionRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.group.repository.GroupRolePermissionRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.UserRegisterDto;
import project.flipnote.user.repository.UserRepository;
import project.flipnote.user.service.UserService;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class GroupServiceTest {

	private static final Logger log = LoggerFactory.getLogger(GroupServiceTest.class);
	//가짜 객체 생성
	@Autowired
	GroupRepository groupRepository;

	//필드에 mock 객체 주입
	@Autowired
	GroupService groupService;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserService userService;

	UserAuth userAuth;

	@Autowired
	GroupPermissionRepository groupPermissionRepository;

	@Autowired
	GroupRolePermissionRepository groupRolePermissionRepository;

	@BeforeEach
	void before() {
		//1. 유저 생성
		UserRegisterDto.Request req = new UserRegisterDto.Request("d@d.com", "1234", "name", "nickname", true, "01000000000", "www.~~");
		UserRegisterDto.Response res = userService.register(req);

		//2. 유저 조회
		User user = userRepository.findById(res.userId()).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);

		//3. 테스트용 userAuth 생성
		userAuth = new UserAuth(user.getId(), user.getEmail(), user.getRole());

		//4. 테스트용 퍼미션 설정
		groupPermissionRepository.saveAll(List.of(
			GroupPermission.builder().name("INVITE").build(),
			GroupPermission.builder().name("KICK").build(),
			GroupPermission.builder().name("JOIN_REQUEST_MANAGE").build()
		));
	}

	@Test
	void 그룹_생성() {
		//given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");
		GroupCreateRequest req1 = new GroupCreateRequest("그룹2", Category.ENGLISH, "설명2", true, true, 50, "www.~~~");

		//when
		GroupCreateResponse response = groupService.create(userAuth, req);
		GroupCreateResponse response1 = groupService.create(userAuth, req1);

		//then
		assertEquals(1L, response.groupId());
		assertEquals(2L, response1.groupId());

		List<GroupRolePermission> groupRolePermissions = groupRolePermissionRepository.findAll();
		groupRolePermissions.forEach(p ->
			log.info("그룹 = {}, 권한 = {}, 역할 = {}", p.getGroup().getId(),p.getGroupPermission().getName(), p.getRole()));
	}

	@Test
	void 그룹_생성_실패_음수_인원() {
		//given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, -100, "www.~~~");

		//when

		//then
		assertThrows(BizException.class, () -> {
			groupService.create(userAuth, req);
		});
	}

	@Test
	void 그룹_생성_실패_양수_인원() {
		//given
		GroupCreateRequest req = new GroupCreateRequest("그룹1", Category.ENGLISH, "설명1", true, true, 200, "www.~~~");

		//when

		//then
		assertThrows(BizException.class, () -> {
			groupService.create(userAuth, req);
		});
	}
}