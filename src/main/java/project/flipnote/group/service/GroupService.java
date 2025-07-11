package project.flipnote.group.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.model.GroupCreateDto;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.user.entity.User;
import project.flipnote.user.exception.UserErrorCode;
import project.flipnote.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final UserRepository userRepository;

	public User findUser(UserAuth userAuth) {
		return userRepository.findById(userAuth.userId()).orElseThrow(
			() -> new BizException(UserErrorCode.USER_NOT_FOUND)
		);
	}

	//그룹 생성
	@Transactional
	public GroupCreateDto.Response create(UserAuth userAuth, GroupCreateDto.@Valid Request req) {
		
		//유저 조회
		User user = findUser(userAuth);
		
		//인원수 검증
		validateMaxMember(req.maxMember());

		//1. 그룹 생성
		Group group = Group.builder()
			.name(req.name())
			.category(req.category())
			.description(req.description())
			.applicationRequired(req.applicationRequired())
			.publicVisible(req.publicVisible())
			.maxMember(req.maxMember())
			.imageUrl(req.image())
			.build();

		groupRepository.save(group);

		log.info("생성 시간: "+group.getCreatedAt());

		//2. 그룹 회원 정보 생성
		GroupMember groupMember = GroupMember.builder()
			.group(group)
			.user(user)
			.role(GroupMemberRole.OWNER)
			.build();

		groupMemberRepository.save(groupMember);

		return GroupCreateDto.Response.from(group.getId());
	}
	
	//인원수 검증
	private void validateMaxMember(int maxMember) {
		if (maxMember < 1 || maxMember > 100) {
			throw new BizException(GroupErrorCode.INVALID_MAX_MEMBER);
		}
	}

}
