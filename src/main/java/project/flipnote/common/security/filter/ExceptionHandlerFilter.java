package project.flipnote.common.security.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import project.flipnote.common.response.ApiResponse;
import project.flipnote.common.security.exception.CustomSecurityException;

@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (CustomSecurityException ex) {
			setErrorResponse(response, ex);
		}
	}

	private void setErrorResponse(HttpServletResponse response, CustomSecurityException ex) throws IOException {
		response.setStatus(ex.getErrorCode().getStatus());
		response.setContentType("application/json; charset=utf-8");

		ApiResponse<Void> errorResponse = ApiResponse.error(ex.getErrorCode());

		response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
	}

}
