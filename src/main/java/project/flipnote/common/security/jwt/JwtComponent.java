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
import lombok.extern.slf4j.Slf4j;
import project.flipnote.auth.entity.UserAuth;
import project.flipnote.auth.model.vo.TokenPair;
import project.flipnote.auth.service.TokenVersionService;
import project.flipnote.common.security.dto.AuthPrinciple;
import project.flipnote.common.security.exception.CustomSecurityException;
import project.flipnote.common.security.exception.SecurityErrorCode;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtComponent {

	private final JwtProperties jwtProperties;
	private final TokenVersionService tokenVersionService;
	private final TokenIdGenerator tokenIdGenerator;

	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
	}

	public TokenPair generateTokenPair(UserAuth userAuth) {
		AuthPrinciple authPrinciple = AuthPrinciple.from(userAuth);

		return generateTokenPair(authPrinciple);
	}

	public TokenPair generateTokenPair(AuthPrinciple authPrinciple) {
		String accessToken = generateAccessToken(authPrinciple);
		String refreshToken = generateRefreshToken(authPrinciple);
		return TokenPair.from(accessToken, refreshToken);
	}

	private String generateAccessToken(AuthPrinciple userAuth) {
		return generateToken(
			userAuth,
			jwtProperties.getAccessTokenExpiredDate(new Date())
		);
	}

	private String generateRefreshToken(AuthPrinciple userAuth) {
		return generateToken(
			userAuth,
			jwtProperties.getRefreshTokenExpiredDate(new Date())
		);
	}

	private String generateToken(AuthPrinciple userAuth, Date expiration) {
		Date now = new Date();

		return Jwts.builder()
			.subject(userAuth.email())
			.id(tokenIdGenerator.generate())
			.claim(JwtConstants.AUTH_ID, userAuth.authId())
			.claim(JwtConstants.USER_ID, userAuth.userId())
			.claim(JwtConstants.ROLE, userAuth.role().name())
			.claim(JwtConstants.TOKEN_VERSION, userAuth.tokenVersion())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}

	public AuthPrinciple extractUserAuthFromToken(String token) {
		Claims claims = parseClaims(token);
		AuthPrinciple userAuth = AuthPrinciple.from(claims);
		validateToken(userAuth);

		return userAuth;
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

	private void validateToken(AuthPrinciple userAuth) {
		long currentTokenVersion = tokenVersionService.findTokenVersion(userAuth.authId())
			.orElseThrow(() -> new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN));

		if (userAuth.tokenVersion() != currentTokenVersion) {
			throw new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN);
		}
	}
}
