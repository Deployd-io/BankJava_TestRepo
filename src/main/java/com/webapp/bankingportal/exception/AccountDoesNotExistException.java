package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class AccountDoesNotExistException extends RuntimeException {
log.debug(">>> Entering AccountDoesNotExistException(message={})", message);

    public AccountDoesNotExistException(String message) {
		log.debug("<<< Exiting AccountDoesNotExistException(message={})", message);
		log.debug(">>> Entering AccountDoesNotExistException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting AccountDoesNotExistException(message={})", message);
}
