package project.flipnote.group.service;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.group.entity.Group;
import project.flipnote.group.model.GroupCreateDto;
import project.flipnote.group.repository.GroupRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupService {
	private final GroupRepository groupRepository;

	public GroupCreateDto.Response create(GroupCreateDto.@Valid Request req) {

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
