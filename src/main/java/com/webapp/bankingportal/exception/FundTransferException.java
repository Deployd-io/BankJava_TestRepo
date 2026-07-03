package com.webapp.bankingportal.exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FundTransferException extends RuntimeException {
public static final Logger log = LoggerFactory.getLogger(FundTransferException.class);

    public FundTransferException(String message) {
        super(message);
		log.info(">>> Entering FundTransferException(message={})", message);
    }

}
