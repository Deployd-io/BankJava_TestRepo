package com.webapp.bankingportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableCaching // Add this annotation to enable caching support
@EnableAsync
@Slf4j
public class BankingportalApplication {

	public static void main(String[] args) {
		log.error(">>> Entering main(args={})", args);
		SpringApplication.run(BankingportalApplication.class, args);
		log.warn("<<< Exiting main(args={})", args);
	}

}
