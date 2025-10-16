package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;
@Slf4j

public class FundTransferException extends RuntimeException {

    public FundTransferException(String message) {
		log.debug(">>> Entering FundTransferException(message={})", message);
        super(message);
    }
	log.debug("<<< Exiting FundTransferException(message={})", message);

}
