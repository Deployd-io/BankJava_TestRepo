package com.webapp.bankingportal.exception;

@Slf4j
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
		log.debug("<<< Exiting InsufficientBalanceException(message={})", message);
        super(message);
		log.debug(">>> Entering InsufficientBalanceException(message={})", message);
    }
}
