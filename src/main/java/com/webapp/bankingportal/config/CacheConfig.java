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
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableCaching
@Slf4j
public class CacheConfig {

	log.debug(">>> Entering cacheManager()");
	log.debug(">>> Entering CaffeineCacheManager()");
    @Bean
    public CacheManager cacheManager() {
		log.debug("cacheManager(): cacheManager → {}", cacheManager);
		log.debug("<<< Exiting CaffeineCacheManager()");
		log.debug(">>> Entering cacheManager()");
		log.debug(">>> Entering CaffeineCacheManager()");
		log.debug("<<< Exiting cacheManager()");
        val cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(List.of("otpAttempts")); // Define the cache name
		log.debug("cacheManager(): cacheManager → {}", cacheManager);
		log.debug(">>> Entering caffeineConfig()");
		log.debug("<<< Exiting CaffeineCacheManager()");
		log.debug("<<< Exiting caffeineConfig()");
        cacheManager.setCaffeine(caffeineConfig());
		log.debug("<<< Exiting cacheManager()");
        return cacheManager;
    }

    public Caffeine<Object, Object> caffeineConfig() {
		log.debug(">>> Entering caffeineConfig()");
        return Caffeine.newBuilder()
		log.debug("<<< Exiting caffeineConfig()");
                .expireAfterWrite(15, TimeUnit.MINUTES) // Cache entries expire after 15 minutes
                .maximumSize(100) // Maximum of 100 entries in the cache
                .recordStats(); // For monitoring cache statistics (optional)
    }

}
