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
import project.flipnote.common.security.dto.UserPrincipal;
import project.flipnote.common.security.exception.SecurityErrorCode;
import project.flipnote.common.security.exception.CustomSecurityException;
import project.flipnote.user.entity.UserProfile;

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

	public TokenPair generateTokenPair(UserProfile userProfile) {
		UserPrincipal userPrincipal = UserPrincipal.from(userProfile);

		return generateTokenPair(userPrincipal);
	}

	public TokenPair generateTokenPair(UserPrincipal userPrincipal) {
		String accessToken = generateAccessToken(userPrincipal);
		String refreshToken = generateRefreshToken(userPrincipal);
		return TokenPair.from(accessToken, refreshToken);
	}

	private String generateAccessToken(UserPrincipal userPrincipal) {
		return generateToken(
			userPrincipal,
			jwtProperties.getAccessTokenExpiredDate(new Date())
		);
	}

	private String generateRefreshToken(UserPrincipal userPrincipal) {
		return generateToken(
			userPrincipal,
			jwtProperties.getRefreshTokenExpiredDate(new Date())
		);
	}

	private String generateToken(UserPrincipal userPrincipal, Date expiration) {
		Date now = new Date();

		return Jwts.builder()
			.subject(userPrincipal.email())
			.id(String.valueOf(userPrincipal.userId()))
			.claim(JwtConstants.ROLE, userPrincipal.userRole().name())
			.claim(JwtConstants.TOKEN_VERSION, userPrincipal.tokenVersion())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}

	public UserPrincipal extractUserAuthFromToken(String token) {
		Claims claims = parseClaims(token);
		UserPrincipal userPrincipal = UserPrincipal.from(claims);
		validateToken(userPrincipal);

		return userPrincipal;
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

	private void validateToken(UserPrincipal userPrincipal) {
		long currentTokenVersion = tokenVersionService.findTokenVersion(userPrincipal.userId())
			.orElseThrow(() -> new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN));

		if (userPrincipal.tokenVersion() != currentTokenVersion) {
			throw new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN);
		}
	}
}
