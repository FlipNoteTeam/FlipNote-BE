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
import project.flipnote.common.security.dto.UserAuth;
import project.flipnote.common.security.exception.SecurityErrorCode;
import project.flipnote.common.security.exception.SecurityException;
import project.flipnote.user.entity.UserRole;

@RequiredArgsConstructor
@Component
public class JwtComponent {

	private final JwtProperties jwtProperties;
	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
	}

	public TokenPair generateTokenPair(User user) {
		String accessToken = generateAccessToken(user);
		String refreshToken = generateRefreshToken(user);
		return TokenPair.from(accessToken, refreshToken);
	}

	private String generateAccessToken(User user) {
		return generateToken(
			user,
			jwtProperties.getAccessTokenExpiredDate(new Date())
		);
	}

	private String generateRefreshToken(User user) {
		return generateToken(
			user,
			jwtProperties.getRefreshTokenExpiredDate(new Date())
		);
	}

	private String generateToken(User user, Date expiration) {
		Date now = new Date();

		return Jwts.builder()
			.subject(user.getEmail())
			.id(String.valueOf(user.getId()))
			.claim(JwtConstants.ROLE, user.getRole().name())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}

	public UserAuth extractUserAuthFromToken(String token) {
		Claims claims = parseClaims(token);

		return extractUserAuthFromClaims(claims);
	}

	private Claims parseClaims(String token) {
		try {
			return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		} catch (ExpiredJwtException expiredJwtException) {
			throw new SecurityException(SecurityErrorCode.TOKEN_EXPIRED);
		} catch (Exception ex) {
			throw new SecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN);
		}
	}

	private UserAuth extractUserAuthFromClaims(Claims claims) {
		long userId = Long.parseLong(claims.getId());
		UserRole userRole = UserRole.from(
			claims.get(JwtConstants.ROLE, String.class)
		);
		String email = claims.getSubject();

		return new UserAuth(userId, email, userRole);
	}
}
