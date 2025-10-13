package com.webapp.bankingportal.exception;

@Slf4j
public class FundTransferException extends RuntimeException {

    public FundTransferException(String message) {
        super(message);
		log.debug(">>> Entering FundTransferException(message={})", message);
    }
	log.debug("<<< Exiting FundTransferException(message={})", message);

}
