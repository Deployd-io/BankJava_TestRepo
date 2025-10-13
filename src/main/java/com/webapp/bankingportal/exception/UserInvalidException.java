package com.webapp.bankingportal.exception;

@Slf4j
public class UserInvalidException extends RuntimeException {

    public UserInvalidException(String message) {
		log.debug(">>> Entering UserInvalidException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting UserInvalidException(message={})", message);
}
