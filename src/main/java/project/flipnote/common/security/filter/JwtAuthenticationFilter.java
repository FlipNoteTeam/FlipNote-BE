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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.security.dto.AuthPrinciple;
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
		System.out.println(token);

		if (StringUtils.hasText(token)) {
			AuthPrinciple userAuth = jwtComponent.extractUserAuthFromToken(token);
			if (userAuth != null) {
				setAuthentication(userAuth, token, request);
			}
		}

		filterChain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (JwtConstants.ACCESS_TOKEN.equals(cookie.getName())) {
					String token = cookie.getValue();
					if (StringUtils.hasText(token)) {
						return token;
					}
				}
			}
		}
		return null;
	}

	private void setAuthentication(AuthPrinciple userAuth, String token, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userAuth, token, userAuth.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
