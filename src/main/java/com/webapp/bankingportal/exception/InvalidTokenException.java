package com.webapp.bankingportal.exception;

@Slf4j
public class InvalidTokenException extends Exception {

    public InvalidTokenException(String message) {
		log.debug("<<< Exiting InvalidTokenException(message={})", message);
        super(message);
		log.debug(">>> Entering InvalidTokenException(message={})", message);
    }
}
