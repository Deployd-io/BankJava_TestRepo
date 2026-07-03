package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeolocationException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(GeolocationException.class);

    public GeolocationException(String message) {
        super(message);
		log.info(">>> Entering GeolocationException(message={})", message);
    }
}
