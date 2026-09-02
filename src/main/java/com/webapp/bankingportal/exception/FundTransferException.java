package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FundTransferException extends RuntimeException {

    public FundTransferException(String message) {
        super(message);
		log.warn("ENTERING: FundTransferException() method");
    }

}
