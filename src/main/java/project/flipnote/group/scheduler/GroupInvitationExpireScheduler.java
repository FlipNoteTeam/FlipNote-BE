package project.flipnote.group.scheduler;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.group.repository.GroupInvitationRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class GroupInvitationExpireScheduler {

	private final GroupInvitationRepository groupInvitationRepository;

	@Transactional
	@SchedulerLock(
		name = "GroupInvitationExpireScheduler_ExpireJob",
		lockAtLeastFor = "PT59M",
		lockAtMostFor = "PT65M"
	)
	@Scheduled(cron = "0 0 * * * *")
	public void runExpireJob() {
		LocalDateTime now = LocalDateTime.now();

		log.info("[GroupInvitationExpireScheduler] 만료 처리 시작 - 기준 시각: {}", now);

		// TODO: 배치로 리팩토링 필요
		int updatedCount = groupInvitationRepository.bulkExpire(now);

		log.info("[GroupInvitationExpireScheduler] 만료 처리 완료 - 변경 건수: {}", updatedCount);
	}
}
