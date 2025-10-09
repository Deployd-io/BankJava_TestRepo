package com.webapp.bankingportal.exception;

@Slf4j
public class InvalidOtpException extends RuntimeException {

    public InvalidOtpException(String msg) {
		log.debug("<<< Exiting InvalidOtpException(msg={})", msg);
        super(msg);
		log.debug(">>> Entering InvalidOtpException(msg={})", msg);
    }
}
