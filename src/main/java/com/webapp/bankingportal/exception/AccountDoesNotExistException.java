package com.webapp.bankingportal.exception;

@Slf4j
public class AccountDoesNotExistException extends RuntimeException {

    public AccountDoesNotExistException(String message) {
		log.debug("<<< Exiting AccountDoesNotExistException(message={})", message);
        super(message);
		log.debug(">>> Entering AccountDoesNotExistException(message={})", message);
    }
}
