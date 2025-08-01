package project.flipnote.common.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.service.TokenVersionService;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.common.security.exception.SecurityErrorCode;
import project.flipnote.common.security.exception.CustomSecurityException;
import project.flipnote.user.entity.User;

@RequiredArgsConstructor
@Component
public class JwtComponent {

	private final JwtProperties jwtProperties;
	private final TokenVersionService tokenVersionService;
	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
	}

	public TokenPair generateTokenPair(User user) {
		AuthPrinciple authPrinciple = AuthPrinciple.from(user);

		return generateTokenPair(authPrinciple);
	}

	public TokenPair generateTokenPair(AuthPrinciple authPrinciple) {
		String accessToken = generateAccessToken(authPrinciple);
		String refreshToken = generateRefreshToken(authPrinciple);
		return TokenPair.from(accessToken, refreshToken);
	}

	private String generateAccessToken(AuthPrinciple authPrinciple) {
		return generateToken(
			authPrinciple,
			jwtProperties.getAccessTokenExpiredDate(new Date())
		);
	}

	private String generateRefreshToken(AuthPrinciple authPrinciple) {
		return generateToken(
			authPrinciple,
			jwtProperties.getRefreshTokenExpiredDate(new Date())
		);
	}

	private String generateToken(AuthPrinciple authPrinciple, Date expiration) {
		Date now = new Date();

		return Jwts.builder()
			.subject(authPrinciple.email())
			.id(String.valueOf(authPrinciple.userId()))
			.claim(JwtConstants.ROLE, authPrinciple.userRole().name())
			.claim(JwtConstants.TOKEN_VERSION, authPrinciple.tokenVersion())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}

	public AuthPrinciple extractUserAuthFromToken(String token) {
		Claims claims = parseClaims(token);
		AuthPrinciple authPrinciple = AuthPrinciple.from(claims);
		validateToken(authPrinciple);

		return authPrinciple;
	}

	public long getExpirationMillis(String token) {
		try {
			Claims claims = parseClaims(token);
			Date expiration = claims.getExpiration();

			return expiration.getTime();
		} catch (Exception e) {
			return 0L;
		}
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException expiredJwtException) {
			throw new CustomSecurityException(SecurityErrorCode.TOKEN_EXPIRED);
		} catch (Exception ex) {
			throw new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN);
		}
	}

	private void validateToken(AuthPrinciple authPrinciple) {
		long currentTokenVersion = tokenVersionService.findTokenVersion(authPrinciple.userId())
			.orElseThrow(() -> new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN));

		if (authPrinciple.tokenVersion() != currentTokenVersion) {
			throw new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN);
		}
	}
}
