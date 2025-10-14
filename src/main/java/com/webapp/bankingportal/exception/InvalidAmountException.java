package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class InvalidAmountException extends RuntimeException {
	log.debug(">>> Entering InvalidAmountException(message={})", message);
    
    public InvalidAmountException(String message) {
		log.debug("<<< Exiting InvalidAmountException(message={})", message);
		log.debug(">>> Entering InvalidAmountException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting InvalidAmountException(message={})", message);
}
