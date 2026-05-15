package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidAmountException extends RuntimeException {
	public static final Logger log = LoggerFactory.getLogger(InvalidAmountException.class);
    
    public InvalidAmountException(String message) {
        super(message);
		log.debug(">>> Entering InvalidAmountException(message={})", message);
    }
}
