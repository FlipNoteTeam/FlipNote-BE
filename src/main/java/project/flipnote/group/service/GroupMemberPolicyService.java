package project.flipnote.group.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.group.entity.Group;
import project.flipnote.group.entity.GroupMember;
import project.flipnote.group.entity.GroupMemberRole;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.repository.GroupMemberRepository;
import project.flipnote.group.repository.GroupRepository;
import project.flipnote.user.entity.UserProfile;

@RequiredArgsConstructor
@Service
public class GroupMemberPolicyService {

	private final GroupRepository groupRepository;
	private final GroupMemberRepository groupMemberRepository;
	private final EntityManager em;

	@Transactional
	public void addGroupMember(Long inviteeUserId, Long groupId) {
		Group lockedGroup = groupRepository.findByIdForUpdate(groupId)
			.orElseThrow(() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND));

		lockedGroup.validateJoinable();
		if (groupMemberRepository.existsByGroup_idAndUser_id(groupId, inviteeUserId)) {
			throw new BizException(GroupErrorCode.ALREADY_GROUP_MEMBER);
		}

		GroupMember groupMember = GroupMember.builder()
			.group(groupRepository.getReferenceById(groupId))
			.user(em.getReference(UserProfile.class, inviteeUserId))
			.role(GroupMemberRole.MEMBER)
			.build();

		groupMemberRepository.save(groupMember);
		lockedGroup.increaseMemberCount();
	}
}
