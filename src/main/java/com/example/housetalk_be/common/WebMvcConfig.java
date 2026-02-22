package com.example.housetalk_be.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** 요청을 C:/Users/human-22/Desktop/img_file/로 매핑
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///C:/Users/human-22/Desktop/img_file/");
    }
}