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
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        val cacheManager = new CaffeineCacheManager();
		log.info("ENTERING: cacheManager() method");
        cacheManager.setCacheNames(List.of("otpAttempts")); // Define the cache name
        cacheManager.setCaffeine(caffeineConfig());
        return cacheManager;
    }

    public Caffeine<Object, Object> caffeineConfig() {
		log.warn("ENTERING: caffeineConfig() method");
        return Caffeine.newBuilder()
                .expireAfterWrite(15, TimeUnit.MINUTES) // Cache entries expire after 15 minutes
                .maximumSize(100) // Maximum of 100 entries in the cache
                .recordStats(); // For monitoring cache statistics (optional)
    }

}
