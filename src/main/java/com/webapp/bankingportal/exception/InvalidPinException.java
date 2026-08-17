package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidPinException extends RuntimeException {

    public InvalidPinException(String message) {
        super(message);
		log.debug(">>> Entering InvalidPinException(message={})", message);
    }
}
