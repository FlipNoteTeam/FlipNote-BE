package project.flipnote;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
class FlipnoteApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(FlipnoteApplicationTests.class);

	@Test
	void contextLoads() {
		log.info("테스트 확인");
	}

}
