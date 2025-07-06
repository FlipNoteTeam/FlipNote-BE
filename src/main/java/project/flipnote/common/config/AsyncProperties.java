package project.flipnote.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "async")
@Component
public class AsyncProperties {

	@Min(value = 1)
	private int corePoolSize;

	@Min(value = 1)
	private int maxPoolSize;

	@Min(value = 1)
	private int queueCapacity;
}
