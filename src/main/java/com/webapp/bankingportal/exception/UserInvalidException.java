package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class UserInvalidException extends RuntimeException {

    public UserInvalidException(String message) {
        super(message);
		log.debug(">>> Entering UserInvalidException(message={})", message);
		log.debug("<<< Exiting UserInvalidException(message={})", message);
    }
}
