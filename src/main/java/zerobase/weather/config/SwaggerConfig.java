package zerobase.weather.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("날씨 일기 프로젝트 API 문서")
                        .description("날씨 일기 서비스 백엔드 API 문서입니다.")
                        .version("2.0.0")
                        .contact(new Contact()
                                .name("김민영")
                                .email("minyoung@example.com")
                        ));

    }
}
