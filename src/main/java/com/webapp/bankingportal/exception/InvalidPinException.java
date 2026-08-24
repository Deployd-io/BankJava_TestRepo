package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidPinException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(InvalidPinException.class);

    public InvalidPinException(String message) {
        super(message);
		log.debug(">>> Entering InvalidPinException(message={})", message);
    }
}
