package com.webapp.bankingportal.exception;

@Slf4j
public class GeolocationException extends RuntimeException {

    public GeolocationException(String message) {
		log.debug(">>> Entering GeolocationException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting GeolocationException(message={})", message);
}
