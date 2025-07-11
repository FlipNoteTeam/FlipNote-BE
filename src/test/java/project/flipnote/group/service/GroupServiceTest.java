package project.flipnote.group.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import project.flipnote.group.model.GroupCreateDto;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.model.UserRegisterDto;
import project.flipnote.user.repository.UserRepository;
import project.flipnote.user.service.UserService;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class GroupServiceTest {

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
	}

	@Test
	void 그룹_생성() {
		//given
		GroupCreateDto.Request req = new GroupCreateDto.Request("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");
		GroupCreateDto.Request req1 = new GroupCreateDto.Request("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");

		//when
		GroupCreateDto.Response response = groupService.create(userAuth, req);
		GroupCreateDto.Response response1 = groupService.create(userAuth, req1);

		//then
		assertEquals(1L, response.groupId());
		assertEquals(2L, response1.groupId());
	}

	@Test
	void 그룹_생성_실패_음수_인원() {
		//given
		GroupCreateDto.Request req = new GroupCreateDto.Request("그룹1", Category.ENGLISH, "설명1", true, true, -100, "www.~~~");

		//when

		//then
		assertThrows(BizException.class, () -> {
			groupService.create(userAuth, req);
		});
	}

	@Test
	void 그룹_생성_실패_양수_인원() {
		//given
		GroupCreateDto.Request req = new GroupCreateDto.Request("그룹1", Category.ENGLISH, "설명1", true, true, 200, "www.~~~");

		//when

		//then
		assertThrows(BizException.class, () -> {
			groupService.create(userAuth, req);
		});
	}

	@Test
	@DisplayName("")
	void 그룹_생성시_그룹_회원_추가() {
		//given
		GroupCreateDto.Request req = new GroupCreateDto.Request("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");
		//when
		groupService.create(userAuth, req);

		//then
		// assertEquals(1L, response.groupId());
	}
}