package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InvalidOtpException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(InvalidOtpException.class);

    public InvalidOtpException(String msg) {
        super(msg);
		log.debug(">>> Entering InvalidOtpException(msg={})", msg);
    }
}
