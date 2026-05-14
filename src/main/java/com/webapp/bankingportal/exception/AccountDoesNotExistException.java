package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountDoesNotExistException extends RuntimeException {

    public AccountDoesNotExistException(String message) {
        super(message);
		log.debug(">>> Entering AccountDoesNotExistException(message={})", message);
		log.debug("<<< Exiting AccountDoesNotExistException(message={})", message);
    }
}
