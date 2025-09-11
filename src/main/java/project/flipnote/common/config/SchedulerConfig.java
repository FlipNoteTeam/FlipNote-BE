package project.flipnote.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

import lombok.RequiredArgsConstructor;
import project.flipnote.image.service.ImageCleanService;

@RequiredArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "2m")
@EnableScheduling
@Configuration
public class SchedulerConfig {

	private final ImageCleanService imageCleanService;

	//이미지 참조 제거
	@Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
	// @Scheduled(cron = "0 * * * * *", zone = "Asia/Seoul")
	public void cleanImageRef() {
		imageCleanService.cleanImageRef();
	}
	
	//이미지 제거
	@Scheduled(cron = "0 30 23 * * 0", zone = "Asia/Seoul")
	// @Scheduled(cron = "30 */2 * * * *", zone = "Asia/Seoul")
	public void cleanImage() {
		imageCleanService.cleanImage();
	}
}
