package project.flipnote.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.util.StringUtils;

@Configuration
public class RedissonConfig {

	@Value("${spring.data.redis.host}")
	private String host;

	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.password:}")
	private String password;

	@Value("${spring.data.redis.cluster.nodes:}")
	private String clusterNodes;

	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient() {
		Config config = new Config();

		if (!clusterNodes.isBlank()) {
			config.useClusterServers()
				.addNodeAddress(clusterNodes.split(","))
				.setPassword(StringUtils.isEmpty(password) ? null : password);
		} else {
			config.useSingleServer()
				.setAddress("redis://" + host + ":" + port)
				.setPassword(StringUtils.isEmpty(password) ? null : password);
		}

		return Redisson.create(config);
	}
}
