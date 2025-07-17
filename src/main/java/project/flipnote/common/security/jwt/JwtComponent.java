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
import project.flipnote.common.security.dto.UserAuth;
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
		UserAuth userAuth = UserAuth.from(user);

		return generateTokenPair(userAuth);
	}

	public TokenPair generateTokenPair(UserAuth userAuth) {
		String accessToken = generateAccessToken(userAuth);
		String refreshToken = generateRefreshToken(userAuth);
		return TokenPair.from(accessToken, refreshToken);
	}

	private String generateAccessToken(UserAuth userAuth) {
		return generateToken(
			userAuth,
			jwtProperties.getAccessTokenExpiredDate(new Date())
		);
	}

	private String generateRefreshToken(UserAuth userAuth) {
		return generateToken(
			userAuth,
			jwtProperties.getRefreshTokenExpiredDate(new Date())
		);
	}

	private String generateToken(UserAuth userAuth, Date expiration) {
		Date now = new Date();

		return Jwts.builder()
			.subject(userAuth.email())
			.id(String.valueOf(userAuth.userId()))
			.claim(JwtConstants.ROLE, userAuth.userRole().name())
			.claim(JwtConstants.TOKEN_VERSION, userAuth.tokenVersion())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}

	public UserAuth extractUserAuthFromToken(String token) {
		Claims claims = parseClaims(token);
		UserAuth userAuth = UserAuth.from(claims);
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

	private void validateToken(UserAuth userAuth) {
		long currentTokenVersion = tokenVersionService.findTokenVersion(userAuth.userId())
			.orElseThrow(() -> new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN));

		if (userAuth.tokenVersion() != currentTokenVersion) {
			throw new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN);
		}
	}
}
