package com.goldenage.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * templates, static 폴더 내 리소스 사용을 위한 정적 리소스 설정.
 * 미설정 시 css, js, image 등의 폴더 경로 설정이 안됩니다.
 * @author Heechang
 * */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/templates/", "classpath:/static/");

        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///home/ubuntu/apps/Golden/src/main/resources/static/images/");

        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:///home/ubuntu/apps/Golden/build/libs/upload/");
    }
}