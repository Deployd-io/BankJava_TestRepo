package com.webapp.bankingportal.exception;

@Slf4j
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
		log.debug(">>> Entering InsufficientBalanceException(message={})", message);
    }
	log.debug("<<< Exiting InsufficientBalanceException(message={})", message);
}
