package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
		log.warn("ENTERING: PasswordResetException() method");
		log.warn("PasswordResetException(message)={}): GDPR-AUDIT: Password reset operation performed", message);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
