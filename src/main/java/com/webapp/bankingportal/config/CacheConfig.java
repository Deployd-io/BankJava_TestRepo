package com.webapp.bankingportal.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableCaching
public class CacheConfig {
public static final Logger log = LoggerFactory.getLogger(CacheConfig.class);

    @Bean
    public CacheManager cacheManager() {
		log.info(">>> Entering cacheManager()");
        val cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(List.of("otpAttempts")); // Define the cache name
		log.info("cacheManager(): cacheManager → {}", cacheManager);
        cacheManager.setCaffeine(caffeineConfig());
		log.info("<<< Exiting cacheManager()");
        return cacheManager;
    }

    public Caffeine<Object, Object> caffeineConfig() {
		log.info(">>> Entering caffeineConfig()");
		log.info("<<< Exiting caffeineConfig()");
        return Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES) // Cache entries expire after 15 minutes
                .maximumSize(100) // Maximum of 100 entries in the cache
                .recordStats(); // For monitoring cache statistics (optional)
    }

}
