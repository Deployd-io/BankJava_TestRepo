package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeolocationException extends RuntimeException {

    public GeolocationException(String message) {
        super(message);
		log.warn("ENTERING: GeolocationException() method");
    }
}
