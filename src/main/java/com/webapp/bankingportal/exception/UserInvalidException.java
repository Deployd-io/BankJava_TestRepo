package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class UserInvalidException extends RuntimeException {
log.debug(">>> Entering UserInvalidException(message={})", message);

    public UserInvalidException(String message) {
		log.debug("<<< Exiting UserInvalidException(message={})", message);
		log.debug(">>> Entering UserInvalidException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting UserInvalidException(message={})", message);
}
