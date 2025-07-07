package project.flipnote.groupapplication.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import project.flipnote.group.entity.Category;
import project.flipnote.group.model.GroupCreateDto;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.group.service.GroupService;
import project.flipnote.groupapplication.model.GroupApplicationJoinRequestDto;
import project.flipnote.groupapplication.repository.GroupApplicationRepository;
import project.flipnote.user.model.UserRegisterDto;
import project.flipnote.user.repository.UserRepository;
import project.flipnote.user.service.UserService;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class GroupApplicationServiceTest {
	@Autowired
	GroupApplicationRepository groupApplicationRepository;

	@Autowired
	GroupApplicationService groupApplicationService;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	GroupService groupService;

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@BeforeEach
	void before() {
		GroupCreateDto.Request req = new GroupCreateDto.Request("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");
		groupService.create(req);

		UserRegisterDto.Request userReq = new UserRegisterDto.Request("w@W.com", "1234", "name", "nickname", true, "010-0000-0000", "www.~~");
		userService.register(userReq);

	}

	@Test
	@DisplayName("")
	void 그룹_가입_신청() throws Exception {
	    //given
		GroupApplicationJoinRequestDto.Request req = new GroupApplicationJoinRequestDto.Request("가입신청123");

	    //when
		GroupApplicationJoinRequestDto.Response res = groupApplicationService.joinRequest(1L, 1L, req);
		GroupApplicationJoinRequestDto.Response secRes = groupApplicationService.joinRequest(1L, 1L, req);

		//then
		Assertions.assertEquals(1L, res.groupApplicationId());
		Assertions.assertEquals(2L, secRes.groupApplicationId());

	}

}