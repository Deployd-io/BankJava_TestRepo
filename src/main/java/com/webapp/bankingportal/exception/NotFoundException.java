package com.webapp.bankingportal.exception;

@Slf4j
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
		log.debug(">>> Entering NotFoundException(message={})", message);
    }
	log.debug("<<< Exiting NotFoundException(message={})", message);
}
