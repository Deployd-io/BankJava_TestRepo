package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsufficientBalanceException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(InsufficientBalanceException.class);

    public InsufficientBalanceException(String message) {
        super(message);
		log.info(">>> Entering InsufficientBalanceException(message={})", message);
    }
}
