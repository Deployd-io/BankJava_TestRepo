package com.webapp.bankingportal.exception;

@Slf4j
public class OtpRetryLimitExceededException extends RuntimeException {

    public OtpRetryLimitExceededException(String message) {
		log.debug(">>> Entering OtpRetryLimitExceededException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting OtpRetryLimitExceededException(message={})", message);
}
