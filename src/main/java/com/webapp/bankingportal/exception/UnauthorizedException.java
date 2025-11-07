package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
		log.warn("UnauthorizedException(message)={}): SOC2-AUDIT: Authorization operation completed", message);
    }
}
