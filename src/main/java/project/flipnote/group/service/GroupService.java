package project.flipnote.group.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupRole;
import project.flipnote.group.model.GroupCreateDto;
import project.flipnote.group.repository.GroupRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
	private final GroupRepository groupRepository;

	@Transactional
	public GroupCreateDto.Response create(/*@AuthenticationPrincipal UserPrincipal userPrincipal, */GroupCreateDto.@Valid Request req) {
		
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

		return GroupCreateDto.Response.from(group.getId());
	}
}
