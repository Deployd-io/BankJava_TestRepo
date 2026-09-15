package com.webapp.bankingportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
		log.info(">>> Entering addCorsMappings(registry={})", registry);
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
				log.info("<<< Exiting addCorsMappings(registry={})", registry);
    }
}
