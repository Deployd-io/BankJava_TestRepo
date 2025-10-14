package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class GeolocationException extends RuntimeException {
log.debug(">>> Entering GeolocationException(message={})", message);

    public GeolocationException(String message) {
		log.debug("<<< Exiting GeolocationException(message={})", message);
		log.debug(">>> Entering GeolocationException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting GeolocationException(message={})", message);
}
