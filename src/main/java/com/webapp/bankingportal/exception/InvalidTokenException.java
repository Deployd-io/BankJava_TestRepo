package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidTokenException extends Exception {
public static final Logger log = LoggerFactory.getLogger(InvalidTokenException.class);

    public InvalidTokenException(String message) {
        super(message);
		log.debug(">>> Entering InvalidTokenException(message={})", message);
    }
}
