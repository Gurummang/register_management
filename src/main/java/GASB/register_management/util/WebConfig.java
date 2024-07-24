package GASB.register_management.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 모든 경로에 대해 CORS 설정을 적용
                .allowedOrigins("*")    // grummang.com -> allow all
//                .allowedOrigins("http://www.grummang.com", "https://www.grummang.com")  // 허용할 출처
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")  // 허용할 HTTP 메소드
                .allowedHeaders("*")  // 허용할 헤더
                .allowCredentials(true);  // 인증 정보 허용
    }
}
