package project.flipnote.groupapplication.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import project.flipnote.group.model.GroupCreateDto;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.groupapplication.model.GroupApplicationJoinRequestDto;

@Service
@RequiredArgsConstructor
public class GroupApplicationService {
	private final GroupRepository groupRepository;

	public GroupApplicationJoinRequestDto.Response joinRequest(Long userId, Long groupId, GroupApplicationJoinRequestDto.Request req) {
		return GroupApplicationJoinRequestDto.Response.from(1L);
	}
}
