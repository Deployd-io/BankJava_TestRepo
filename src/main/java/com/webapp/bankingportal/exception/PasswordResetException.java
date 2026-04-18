package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
		log.debug(">>> Entering PasswordResetException(message={})", message);
		log.debug("<<< Exiting PasswordResetException(message={},cause={})", message, cause);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
