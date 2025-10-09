package com.webapp.bankingportal.exception;

@Slf4j
public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
		log.debug(">>> Entering PasswordResetException(message={},cause={})", message, cause);
    }
	log.debug("<<< Exiting PasswordResetException(message={},cause={})", message, cause);
}
