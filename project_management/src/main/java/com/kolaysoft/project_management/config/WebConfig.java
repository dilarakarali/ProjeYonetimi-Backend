package com.kolaysoft.project_management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Backend'e gelen TÜM yollara ("/auth/**", "/projects/**", vb.) izin ver
                registry.addMapping("/**")
                        // SADECE bu adresten gelen isteklere izin ver
                        .allowedOrigins("https://projeyonetimifrontend.onrender.com")
                        // İzin verilen HTTP metotları
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // Gelen isteklerdeki tüm header'lara izin ver
                        .allowedHeaders("*")
                        // Kimlik bilgileri (cookie, token vb.) gönderimine izin ver
                        .allowCredentials(true);
            }
        };
    }
}