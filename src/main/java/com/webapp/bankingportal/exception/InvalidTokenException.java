package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class InvalidTokenException extends Exception {
log.debug(">>> Entering InvalidTokenException(message={})", message);

    public InvalidTokenException(String message) {
		log.debug("<<< Exiting InvalidTokenException(message={})", message);
		log.debug(">>> Entering InvalidTokenException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting InvalidTokenException(message={})", message);
}
