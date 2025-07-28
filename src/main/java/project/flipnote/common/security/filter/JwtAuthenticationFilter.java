package project.flipnote.common.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.security.dto.AccountAuth;
import project.flipnote.common.security.jwt.JwtComponent;
import project.flipnote.common.security.jwt.JwtConstants;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtComponent jwtComponent;

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		String token = extractToken(request);

		if (StringUtils.hasText(token)) {
			AccountAuth accountAuth = jwtComponent.extractUserAuthFromToken(token);
			if (accountAuth != null) {
				setAuthentication(accountAuth, token, request);
			}
		}

		filterChain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(JwtConstants.AUTH_HEADER);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtConstants.TOKEN_PREFIX)) {
			return bearerToken.substring(JwtConstants.TOKEN_PREFIX.length());
		}
		return null;
	}

	private void setAuthentication(AccountAuth accountAuth, String token, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(accountAuth, token, accountAuth.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
