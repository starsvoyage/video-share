package edu.arizona.videoshare.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(System.getProperty("user.dir"), "uploads").toAbsolutePath().normalize();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");
    }
}