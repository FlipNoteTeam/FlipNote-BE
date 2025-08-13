package project.flipnote.group.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.exception.CommonErrorCode;
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
	private final RedissonClient redissonClient;

	@Transactional
	public void addGroupMember(Long inviteeUserId, Long groupId) {
		// TODO: AOP로 분산락 적용할 수 있도록 수정 예정
		String lockKey = "group_member_lock:" + groupId;
		RLock lock = redissonClient.getLock(lockKey);

		boolean isLocked = false;
		try {
			isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
			if (!isLocked) {
				throw new BizException(CommonErrorCode.SERVICE_TEMPORARILY_UNAVAILABLE);
			}

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
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while trying to acquire distributed lock", e);
		} finally {
			if (isLocked && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}
