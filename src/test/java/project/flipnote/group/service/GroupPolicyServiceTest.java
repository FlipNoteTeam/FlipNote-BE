package project.flipnote.group.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.test.util.ReflectionTestUtils;

import project.flipnote.common.exception.BizException;
import project.flipnote.group.entity.Category;
import project.flipnote.group.entity.Group;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.model.GroupPutRequest;
import project.flipnote.group.repository.GroupRepository;

@ExtendWith(MockitoExtension.class)
class GroupPolicyServiceTest {

	// @InjectMocks
	// GroupPolicyService groupPolicyService;
	//
	// @Mock
	// GroupRepository groupRepository;
	//
	// @Mock
	// RedissonClient redissonClient;
	//
	// @Mock
	// RLock rLock;
	//
	// @Test
	// void 실패_유저수보다_작게_변경() throws Exception {
	// 	Long groupId = 1L;
	// 	Group group = Group.builder()
	// 		.name("그룹1")
	// 		.category(Category.IT)
	// 		.description("설명1")
	// 		.publicVisible(true)
	// 		.applicationRequired(true)
	// 		.maxMember(100)
	// 		.imageUrl("www.~~~")
	// 		.build();
	//
	// 	ReflectionTestUtils.setField(group, "id", 1L);
	// 	ReflectionTestUtils.setField(group, "memberCount", 100);
	//
	// 	GroupPutRequest req = new GroupPutRequest("그룹1", Category.ENGLISH, "설명1", true, true, 50, 1L);
	//
	// 	given(redissonClient.getLock(anyString())).willReturn(rLock);
	// 	given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
	// 	given(rLock.isHeldByCurrentThread()).willReturn(true);
	// 	given(groupRepository.findByIdForUpdate(groupId)).willReturn(Optional.of(group));
	//
	// 	String url = "www.~~.com";
	//
	// 	//when & then
	// 	BizException exception =
	// 		assertThrows(BizException.class, () -> groupPolicyService.changeGroup(groupId, req, url));
	//
	// 	assertEquals(GroupErrorCode.INVALID_MEMBER_COUNT, exception.getErrorCode());
	// 	then(rLock).should().unlock();
	// }
	//
	// @Test
	// void 그룹_수정_성공() throws Exception {
	// 	Long groupId = 1L;
	// 	Group group = Group.builder()
	// 		.name("그룹1")
	// 		.category(Category.IT)
	// 		.description("설명1")
	// 		.publicVisible(true)
	// 		.applicationRequired(true)
	// 		.maxMember(100)
	// 		.imageUrl("www.~~~")
	// 		.build();
	//
	// 	ReflectionTestUtils.setField(group, "id", 1L);
	// 	ReflectionTestUtils.setField(group, "memberCount", 3);
	//
	// 	GroupPutRequest req = new GroupPutRequest("그룹1", Category.ENGLISH, "설명1", true, true, 50, 1L);
	//
	// 	given(redissonClient.getLock(anyString())).willReturn(rLock);
	// 	given(rLock.tryLock(anyLong(), anyLong(), any())).willReturn(true);
	// 	given(rLock.isHeldByCurrentThread()).willReturn(true);
	// 	given(groupRepository.findByIdForUpdate(groupId)).willReturn(Optional.of(group));
	//
	// 	String url = "www.~~.com";
	// 	//when
	// 	Group changeGroup = groupPolicyService.changeGroup(groupId, req, url);
	//
	// 	assertEquals(req.name(), changeGroup.getName());
	// 	assertEquals(req.category(), changeGroup.getCategory());
	//
	// }
}
