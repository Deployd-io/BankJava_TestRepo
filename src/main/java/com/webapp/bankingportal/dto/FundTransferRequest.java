package com.webapp.bankingportal.dto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record FundTransferRequest(String sourceAccountNumber, String targetAccountNumber, double amount, String pin,
        String remarks) {
		private static final Logger log = LoggerFactory.getLogger(FundTransferRequest.class);

    public FundTransferRequest(String sourceAccountNumber, String targetAccountNumber, double amount, String pin) {
        this(sourceAccountNumber, targetAccountNumber, amount, pin, null);
		log.error(">>> Entering FundTransferRequest(sourceAccountNumber={},targetAccountNumber={},amount={},pin=<redacted>)", sourceAccountNumber, targetAccountNumber, amount);
    log.warn("<<< Exiting FundTransferRequest(sourceAccountNumber={},targetAccountNumber={},amount={},pin=<redacted>)", sourceAccountNumber, targetAccountNumber, amount);
    }
}
