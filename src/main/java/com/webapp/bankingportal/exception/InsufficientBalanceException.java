package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class InsufficientBalanceException extends RuntimeException {
log.debug(">>> Entering InsufficientBalanceException(message={})", message);

    public InsufficientBalanceException(String message) {
		log.debug("<<< Exiting InsufficientBalanceException(message={})", message);
		log.debug(">>> Entering InsufficientBalanceException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting InsufficientBalanceException(message={})", message);
}
