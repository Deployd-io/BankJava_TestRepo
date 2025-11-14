package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
		log.warn("PasswordResetException(message)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", message);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
    }
}
