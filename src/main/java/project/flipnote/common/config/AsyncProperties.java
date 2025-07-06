package project.flipnote.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "async")
@Component
public class AsyncProperties {

	@Positive
	private int corePoolSize;

	@Positive
	private int maxPoolSize;

	@Positive
	private int queueCapacity;
}
