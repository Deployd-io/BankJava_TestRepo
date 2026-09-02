package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvalidOtpException extends RuntimeException {

    public InvalidOtpException(String msg) {
        super(msg);
		log.warn("ENTERING: InvalidOtpException() method");
    }
}
