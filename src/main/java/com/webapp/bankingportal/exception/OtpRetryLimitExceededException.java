package com.webapp.bankingportal.exception;

@Slf4j
public class OtpRetryLimitExceededException extends RuntimeException {

    public OtpRetryLimitExceededException(String message) {
        super(message);
		log.debug(">>> Entering OtpRetryLimitExceededException(message={})", message);
    }
	log.debug("<<< Exiting OtpRetryLimitExceededException(message={})", message);
}
