package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordResetException extends RuntimeException {
	public static final Logger log = LoggerFactory.getLogger(PasswordResetException.class);
    public PasswordResetException(String message) {
        super(message);
		log.debug(">>> Entering PasswordResetException(message={})", message);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
