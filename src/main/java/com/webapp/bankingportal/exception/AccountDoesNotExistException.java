package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountDoesNotExistException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(AccountDoesNotExistException.class);

    public AccountDoesNotExistException(String message) {
        super(message);
		log.info(">>> Entering AccountDoesNotExistException(message={})", message);
    }
}
