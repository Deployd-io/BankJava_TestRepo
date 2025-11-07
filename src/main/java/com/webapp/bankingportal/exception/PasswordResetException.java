package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
		log.warn("PasswordResetException(message,cause)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed", message, cause);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
