package project.flipnote.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "async")
@Component
public class AsyncProperties {
	private int corePoolSize;
	private int maxPoolSize;
	private int queueCapacity;
}
