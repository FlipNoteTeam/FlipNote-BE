package project.flipnote.group.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;
import project.flipnote.group.entity.Category;
import project.flipnote.group.model.GroupCreateDto;
import project.flipnote.group.repository.GroupRepository;

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

	@Test
	void 그룹_생성() {
		//given
		GroupCreateDto.Request req = new GroupCreateDto.Request("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");
		GroupCreateDto.Request req1 = new GroupCreateDto.Request("그룹1", Category.ENGLISH, "설명1", true, true, 100, "www.~~~");

		//when
		GroupCreateDto.Response response = groupService.create(req);
		GroupCreateDto.Response response1 = groupService.create(req1);

		//then
		assertEquals(1L, response.groupId());
		assertEquals(2L, response1.groupId());
	}

	@Test
	@DisplayName("")
	void 그룹_생성_valid_체크() throws Exception {

	}
}