package com.webapp.bankingportal.exception;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
		log.warn("ENTERING: InsufficientBalanceException() method");
    }
}
