package com.webapp.bankingportal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableCaching // Add this annotation to enable caching support
@EnableAsync
public class BankingportalApplication {
public static final Logger log = LoggerFactory.getLogger(BankingportalApplication.class);

	public static void main(String[] args) {
		log.debug(">>> Entering main(args={})", args);
		SpringApplication.run(BankingportalApplication.class, args);
		log.debug("<<< Exiting main(args={})", args);
	}

}
