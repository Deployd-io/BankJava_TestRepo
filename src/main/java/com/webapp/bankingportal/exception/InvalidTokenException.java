package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidTokenException extends Exception {

    public InvalidTokenException(String message) {
        super(message);
		log.debug(">>> Entering InvalidTokenException(message={})", message);
		log.debug("<<< Exiting InvalidTokenException(message={})", message);
    }
}
