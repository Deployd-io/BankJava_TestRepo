package com.webapp.bankingportal.exception;

@Slf4j
public class InvalidPinException extends RuntimeException {

    public InvalidPinException(String message) {
		log.debug("<<< Exiting InvalidPinException(message={})", message);
        super(message);
		log.debug(">>> Entering InvalidPinException(message={})", message);
    }
}
