package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
		log.debug(">>> Entering UnauthorizedException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting UnauthorizedException(message={})", message);
}
