package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserInvalidException extends RuntimeException {

    public UserInvalidException(String message) {
        super(message);
		log.warn("ENTERING: UserInvalidException() method");
    }
}
