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
import project.flipnote.auth.entity.AuthAccount;
import project.flipnote.auth.model.TokenPair;
import project.flipnote.auth.service.TokenVersionService;
import project.flipnote.common.security.dto.AccountAuth;
import project.flipnote.common.security.exception.CustomSecurityException;
import project.flipnote.common.security.exception.SecurityErrorCode;

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

	public TokenPair generateTokenPair(AuthAccount authAccount) {
		AccountAuth accountAuth = AccountAuth.from(authAccount);

		return generateTokenPair(accountAuth);
	}

	public TokenPair generateTokenPair(AccountAuth accountAuth) {
		String accessToken = generateAccessToken(accountAuth);
		String refreshToken = generateRefreshToken(accountAuth);
		return TokenPair.from(accessToken, refreshToken);
	}

	private String generateAccessToken(AccountAuth accountAuth) {
		return generateToken(
			accountAuth,
			jwtProperties.getAccessTokenExpiredDate(new Date())
		);
	}

	private String generateRefreshToken(AccountAuth accountAuth) {
		return generateToken(
			accountAuth,
			jwtProperties.getRefreshTokenExpiredDate(new Date())
		);
	}

	private String generateToken(AccountAuth accountAuth, Date expiration) {
		Date now = new Date();

		return Jwts.builder()
			.subject(accountAuth.email())
			.id(String.valueOf(accountAuth.accountId()))
			.claim(JwtConstants.ROLE, accountAuth.userRole().name())
			.claim(JwtConstants.TOKEN_VERSION, accountAuth.tokenVersion())
			.issuedAt(now)
			.expiration(expiration)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}

	public AccountAuth extractUserAuthFromToken(String token) {
		Claims claims = parseClaims(token);
		AccountAuth accountAuth = AccountAuth.from(claims);
		validateToken(accountAuth);

		return accountAuth;
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

	private void validateToken(AccountAuth accountAuth) {
		long currentTokenVersion = tokenVersionService.findTokenVersion(accountAuth.accountId())
			.orElseThrow(() -> new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN));

		if (accountAuth.tokenVersion() != currentTokenVersion) {
			throw new CustomSecurityException(SecurityErrorCode.NOT_VALID_JWT_TOKEN);
		}
	}
}
