package com.makeskilled.Techfolio;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from the 'uploaded_videos' directory
        registry.addResourceHandler("/uploaded_videos/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploaded_videos/");
    }
}