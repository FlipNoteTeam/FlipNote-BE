package project.flipnote.common.constants;

import java.time.Duration;

public interface RedisKeys {
	String getPattern();

	int getTtlSeconds();

	default String key(Object... args) {
		if (args == null || args.length == 0) {
			throw new IllegalArgumentException("Arguments cannot be null or empty");
		}

		return String.format(getPattern(), args);
	}

	default Duration getTtl() {
		return Duration.ofSeconds(getTtlSeconds());
	}
}
