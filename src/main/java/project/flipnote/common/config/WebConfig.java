package project.flipnote.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.RequiredArgsConstructor;
import project.flipnote.auth.interceptor.OAuthCookieCleanupInterceptor;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

	private final OAuthCookieCleanupInterceptor oAuthCookieCleanupInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(oAuthCookieCleanupInterceptor)
			.addPathPatterns("/oauth2/callback/**");
	}
}