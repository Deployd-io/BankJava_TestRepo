package com.webapp.bankingportal.exception;

@Slf4j
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
		log.debug(">>> Entering UnauthorizedException(message={})", message);
    }
	log.debug("<<< Exiting UnauthorizedException(message={})", message);
}
