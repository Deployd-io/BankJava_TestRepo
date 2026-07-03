package com.webapp.bankingportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
public static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
		log.info(">>> Entering addCorsMappings(registry={})", registry);
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
				log.info("<<< Exiting addCorsMappings(registry={})", registry);
    }
}
