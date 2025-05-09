package ssabab.back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로 허용
                .allowedOrigins("*") // 모든 Origin 허용
                .allowedMethods("*") // GET, POST, PUT, DELETE 등 모두 허용
                .allowedHeaders("*") // 모든 Header 허용
                .allowCredentials(false);
    }
}
