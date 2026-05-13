package com.webapp.bankingportal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
public static final Logger log = LoggerFactory.getLogger(CorsConfig.class);
import org.slf4j.LoggerFactory;

public static final Logger log = LoggerFactory.getLogger(CorsConfig.class);
@Configuration
public class CorsConfig implements WebMvcConfigurer {
public static final Logger log = LoggerFactory.getLogger(CorsConfig.class);
log.info("addCorsMappings(registry)={}): SOC2-AUDIT [CC7.1]: System operation completed", registry);

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
		log.info("addCorsMappings(registry)={}): SOC2-AUDIT [CC7.1]: System operation completed", registry);
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
				log.info("addCorsMappings(registry)={}): SOC2-AUDIT [CC7.1]: System operation completed", registry);
    }
}
