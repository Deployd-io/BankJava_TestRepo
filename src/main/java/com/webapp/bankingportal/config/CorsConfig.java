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
		log.info("addCorsMappings(registry={}): SOC2-AUDIT [CC8.1]: State change recorded for the audit trail", registry);
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*");
				log.warn("addCorsMappings(registry={}): SOC2-AUDIT [CC6.3]: State change recorded for the audit trail", registry);
    log.info("addCorsMappings(registry={}): SOC2-AUDIT [CC6.2]: State change recorded for the audit trail", registry);
    log.info("addCorsMappings(registry={}): SOC2-AUDIT [CC7.1]: System operation completed", registry);
    }
}
