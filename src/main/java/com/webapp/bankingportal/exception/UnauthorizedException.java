package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnauthorizedException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(UnauthorizedException.class);

    public UnauthorizedException(String message) {
        super(message);
    }
}
