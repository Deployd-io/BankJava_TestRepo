package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
    }

    public PasswordResetException(String message, Throwable cause) {
		log.debug(">>> Entering PasswordResetException(message={},cause={})", message, cause);
        super(message, cause);
    }
	log.debug("<<< Exiting PasswordResetException(message={},cause={})", message, cause);
}
