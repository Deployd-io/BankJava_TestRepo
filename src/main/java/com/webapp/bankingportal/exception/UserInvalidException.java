package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInvalidException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(UserInvalidException.class);

    public UserInvalidException(String message) {
        super(message);
		log.debug(">>> Entering UserInvalidException(message={})", message);
    }
}
