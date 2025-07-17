package project.flipnote.common.security.jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
	private String secret;
	private Duration accessTokenExpiration;
	private Duration refreshTokenExpiration;

	public Date getAccessTokenExpiredDate(Date now) {
		Instant expiredInstant = now.toInstant().plus(accessTokenExpiration);
		return Date.from(expiredInstant);
	}

	public Date getRefreshTokenExpiredDate(Date now) {
		Instant expiredInstant = now.toInstant().plus(refreshTokenExpiration);
		return Date.from(expiredInstant);
	}
}
