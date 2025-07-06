package project.flipnote.common.constants;

import java.time.Duration;

public interface RedisKeys {
	String getPattern();

	int getTtlSeconds();

	default String key(Object... args) {
		return String.format(getPattern(), args);
	}

	default Duration getTtl() {
		return Duration.ofSeconds(getTtlSeconds());
	}
}
