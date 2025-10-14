package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class NotFoundException extends RuntimeException {
log.debug(">>> Entering NotFoundException(message={})", message);

    public NotFoundException(String message) {
		log.debug("<<< Exiting NotFoundException(message={})", message);
		log.debug(">>> Entering NotFoundException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting NotFoundException(message={})", message);
}
