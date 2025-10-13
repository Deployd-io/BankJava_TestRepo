package com.webapp.bankingportal.exception;

@Slf4j
public class InvalidAmountException extends RuntimeException {
    
    public InvalidAmountException(String message) {
		log.debug(">>> Entering InvalidAmountException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting InvalidAmountException(message={})", message);
}
