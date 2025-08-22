package project.flipnote.group.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import project.flipnote.common.exception.BizException;
import project.flipnote.common.exception.CommonErrorCode;
import project.flipnote.group.entity.Group;
import project.flipnote.group.exception.GroupErrorCode;
import project.flipnote.group.model.GroupPutRequest;
import project.flipnote.group.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class GroupPolicyService {
	private final GroupRepository groupRepository;
	private final RedissonClient redissonClient;

	@Transactional
	public Group changeGroup(Long groupId, GroupPutRequest req) {
		String lockKey = "group_lock:" + groupId;
		RLock lock = redissonClient.getLock(lockKey);

		boolean isLocked = false;
		try {
			isLocked = lock.tryLock(2, 3, TimeUnit.SECONDS);
			if (!isLocked) {
				throw new BizException(CommonErrorCode.SERVICE_TEMPORARILY_UNAVAILABLE);
			}

			Group lockedGroup = groupRepository.findByIdForUpdate(groupId)
				.orElseThrow(() -> new BizException(GroupErrorCode.GROUP_NOT_FOUND));

			lockedGroup.validateMaxMemberUpdatable(req.maxMember());

			lockedGroup.changeGroup(req);

			return lockedGroup;

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new BizException(CommonErrorCode.SERVICE_TEMPORARILY_UNAVAILABLE);
		} finally {
			if (isLocked && lock.isHeldByCurrentThread()) {
				lock.unlock();
			}
		}
	}
}
