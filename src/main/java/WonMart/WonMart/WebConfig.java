package WonMart.WonMart;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
 Spring Boot 가 실행되면 Configuration에 등록된 핸들러 정보를 읽어들인다
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String uploadPath;

    public WebConfig() {
        this.uploadPath = System.getProperty("user.home") + "/Desktop/upload/";
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         pathPattern 에 들어있는 이미지 경로 요청이 들어오면
         file:// + uploadPath 에 있는 경로로 자원을 탐색한다는 의미이다
         https://blog.jiniworld.me/28
         */
        registry.addResourceHandler("/img/post/**")
                .addResourceLocations("file://" + uploadPath);
    }

}
