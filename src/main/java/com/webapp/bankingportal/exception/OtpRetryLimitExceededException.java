package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OtpRetryLimitExceededException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(OtpRetryLimitExceededException.class);

    public OtpRetryLimitExceededException(String message) {
        super(message);
		log.debug(">>> Entering OtpRetryLimitExceededException(message={})", message);
    }
}
