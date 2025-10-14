package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class InvalidPinException extends RuntimeException {
log.debug(">>> Entering InvalidPinException(message={})", message);

    public InvalidPinException(String message) {
		log.debug("<<< Exiting InvalidPinException(message={})", message);
		log.debug(">>> Entering InvalidPinException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting InvalidPinException(message={})", message);
}
