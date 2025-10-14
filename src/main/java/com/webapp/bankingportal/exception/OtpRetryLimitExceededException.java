package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class OtpRetryLimitExceededException extends RuntimeException {
log.debug(">>> Entering OtpRetryLimitExceededException(message={})", message);

    public OtpRetryLimitExceededException(String message) {
		log.debug("<<< Exiting OtpRetryLimitExceededException(message={})", message);
		log.debug(">>> Entering OtpRetryLimitExceededException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting OtpRetryLimitExceededException(message={})", message);
}
