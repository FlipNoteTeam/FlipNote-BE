package project.flipnote.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import project.flipnote.common.security.jwt.JwtConstants;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
			.addSecurityItem(
				new SecurityRequirement()
					.addList("access-token")
					.addList("refresh-token-cookie")
			)
			.components(new Components()
				.addSecuritySchemes("access-token",
					new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT"))
				.addSecuritySchemes("refresh-token-cookie",
					new SecurityScheme()
						.type(SecurityScheme.Type.APIKEY)
						.in(SecurityScheme.In.COOKIE)
						.name(JwtConstants.REFRESH_TOKEN)
						.description("refreshToken 쿠키에 JWT 값을 담아주세요.")))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("FlipNote")
			.description("FlipNote API 명세서")
			.version("1.0.0");
	}
}
