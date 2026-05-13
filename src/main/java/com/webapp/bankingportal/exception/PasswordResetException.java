package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public static final Logger log = LoggerFactory.getLogger(PasswordResetException.class);

public class PasswordResetException extends RuntimeException {
	public static final Logger log = LoggerFactory.getLogger(PasswordResetException.class);
	log.warn("PasswordResetException(message)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", message);
    public PasswordResetException(String message) {
        super(message);
		log.warn("PasswordResetException(message)={}): SOC2-AUDIT [CC6.1]: Password reset operation performed", message);
		log.warn("PasswordResetException(message,cause)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed", message, cause);
    }

    public PasswordResetException(String message, Throwable cause) {
        super(message, cause);
		log.warn("PasswordResetException(message,cause)={},{}): SOC2-AUDIT [CC6.1]: Password reset operation performed", message, cause);
    }
}
