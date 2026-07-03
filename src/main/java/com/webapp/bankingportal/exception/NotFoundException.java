package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NotFoundException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(NotFoundException.class);

    public NotFoundException(String message) {
        super(message);
		log.info(">>> Entering NotFoundException(message={})", message);
    }
}
